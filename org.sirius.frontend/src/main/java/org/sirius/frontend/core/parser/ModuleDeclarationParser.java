package org.sirius.frontend.core.parser;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.sirius.common.core.Constants;
import org.sirius.common.core.QName;
import org.sirius.common.error.Reporter;
import org.sirius.frontend.ast.AstClassDeclaration;
import org.sirius.frontend.ast.AstModuleDeclaration;
import org.sirius.frontend.ast.AstPackageDeclaration;
import org.sirius.frontend.ast.AstToken;
import org.sirius.frontend.ast.FunctionDefinition;
import org.sirius.frontend.ast.ModuleImport;
import org.sirius.frontend.ast.ModuleImportEquivalents;
import org.sirius.frontend.ast.QualifiedName;
import org.sirius.frontend.core.parser.FunctionDeclarationParser.FunctionDefinitionVisitor;
import org.sirius.frontend.parser.SLexer;
import org.sirius.frontend.parser.SParser.ClassDeclarationContext;
import org.sirius.frontend.parser.SParser.ConcreteModuleContext;
import org.sirius.frontend.parser.SParser.FunctionDeclarationContext;
import org.sirius.frontend.parser.SParser.FunctionDefinitionContext;
import org.sirius.frontend.parser.SParser.ModuleDeclarationContext;
import org.sirius.frontend.parser.SParser.ModuleImportContext;
import org.sirius.frontend.parser.SParser.ModuleVersionEquivalentContext;
import org.sirius.frontend.parser.SParser.PackageDeclarationContext;
import org.sirius.frontend.parser.SParserBaseVisitor;

/**
 * Visitor-based parser for the 'typeParameterDeclaration' rule.
 * 
 * @author jpragey
 *
 */
public class ModuleDeclarationParser {
	private Reporter reporter;
	private Parsers parsers;
	private CommonTokenStream tokens;

	public ModuleDeclarationParser(Reporter reporter, CommonTokenStream tokens) {
		this.reporter = reporter;
		this.parsers = new Parsers(reporter, tokens);
		this.tokens = tokens;
	}

	public class ModuleImportVisitor extends SParserBaseVisitor<ModuleImport> {
		@Override
		public ModuleImport visitModuleImport(ModuleImportContext ctx) {

			// -- shared
			boolean shared = (ctx.shared != null);

			// -- origin
			Optional<AstToken> origin = (ctx.origin == null) ? Optional.empty() : Optional.of(new AstToken(ctx.origin));

			// -- qname
			Parsers.QualifiedNameVisitor nameVisitor = new Parsers(reporter, tokens).new QualifiedNameVisitor();
			Optional<QName> qname = Optional.empty();
			if (ctx.nameQName != null)
				qname = Optional.of(nameVisitor.visit(ctx.nameQName).toQName());

			// -- qnameString
			Optional<String> qnameString = Optional.ofNullable(ctx.nameString).map((Token tk) -> {
				String rawText = tk.getText();
				String trimmedName = rawText.substring(1, rawText.length() - 1) // remove double quotes
						.trim();
				return trimmedName;

			});

			// -- version
			Token versionToken = ctx.version;
			Token versionStringTk = ctx.versionString;

			ModuleImport moduleImport = new ModuleImport(shared, origin, qname, qnameString, versionToken,
					versionStringTk);
			return moduleImport;
		}
	}

	public static class ImportEquivalentVisitor extends SParserBaseVisitor<Void> {
		ModuleImportEquivalents equivalents;

		public ImportEquivalentVisitor(ModuleImportEquivalents equivalents) {
			super();
			this.equivalents = equivalents;
		}

		@Override
		public Void visitModuleVersionEquivalent(ModuleVersionEquivalentContext ctx) {
			AstToken key = new AstToken(ctx.key);
			AstToken val = new AstToken(ctx.value);

			equivalents.put(key, val);
			return null;
		}
	}

	public class PackageElementVisitor extends SParserBaseVisitor<Void> {
		private List<AstPackageDeclaration> packageDeclarations = new ArrayList<>();
		private PackageElements packageElements;

		public PackageElementVisitor(List<AstPackageDeclaration> packageDeclarations, PackageElements packageElements) {
			super();
			this.packageDeclarations = packageDeclarations;
			this.packageElements = packageElements;
		}

		@Override
		public Void visitPackageDeclaration(PackageDeclarationContext ctx) {
			Parsers.PackageDeclarationVisitor v = parsers.new PackageDeclarationVisitor();
			AstPackageDeclaration partialList = v.visit(ctx);
			this.packageDeclarations.add(partialList);
			return null;
		}

		@Override
		public Void visitFunctionDeclaration(FunctionDeclarationContext ctx) {
			FunctionDefinitionVisitor v = new FunctionDeclarationParser(reporter,
					tokens).new FunctionDefinitionVisitor();

			FunctionDefinition functionDefinition = v.visit(ctx);
			this.packageElements.functiondefinitions.add(functionDefinition);
			return null;
		}

		@Override
		public Void visitFunctionDefinition(FunctionDefinitionContext ctx) {
			FunctionDefinitionVisitor v = new FunctionDeclarationParser(reporter,
					tokens).new FunctionDefinitionVisitor();

			FunctionDefinition functionDefinition = v.visit(ctx);
			this.packageElements.functiondefinitions.add(functionDefinition);
			return null;
		}

		@Override
		public Void visitClassDeclaration(ClassDeclarationContext ctx) {
			ClassDeclarationParser.ClassDeclarationVisitor visitor = new ClassDeclarationParser(reporter,
					tokens).new ClassDeclarationVisitor();

			AstClassDeclaration cd = visitor.visit(ctx);
			this.packageElements.classDeclarations.add(cd);
			return null;
		}
	}

	public static class PackageElements {
		public List<AstClassDeclaration> classDeclarations = new ArrayList<>();
		public List<FunctionDefinition> functiondefinitions = new ArrayList<>();

		public boolean isEmpty() {
			return classDeclarations.isEmpty() && functiondefinitions.isEmpty();
		}
	}

	public class ConcreteModuleVisitor extends SParserBaseVisitor<AstModuleDeclaration> {
//		CommonTokenStream tokens;
		public ConcreteModuleVisitor(/* CommonTokenStream tokens */) {
			super();
//			this.tokens = tokens;
		}

		@Override
		public AstModuleDeclaration visitConcreteModule(ConcreteModuleContext ctx) {
			PackageElements packageElements = new PackageElements();

			// -- package content
			LinkedList<AstPackageDeclaration> packageDeclarations = new LinkedList<>();
			PackageElementVisitor packageElementVisitor = new PackageElementVisitor(packageDeclarations,
					packageElements);

			ctx.packageElement().forEach(peContext -> packageElementVisitor.visit(peContext));

			// -- Module declaration
			AstModuleDeclaration result;
			ModuleDeclarationContext moduleDeclarationContext = ctx.moduleDeclaration();
			if (moduleDeclarationContext != null) { // Explicit module
				ModuleDeclarationVisitor mdVisitor = new ModuleDeclarationVisitor(reporter, tokens);
				AstModuleDeclarationBuilder mdBuilder = mdVisitor.visit(moduleDeclarationContext);

				List<AstPackageDeclaration> pds;
				if (packageDeclarations.isEmpty()) {
					// -- Create default package
					QName pkgQName = mdBuilder.getQualifiedName(); // use module qname as package qname
					AstPackageDeclaration defaultPackageDeclaration = new AstPackageDeclaration(reporter,
							Optional.of(pkgQName), packageElements.functiondefinitions,
							packageElements.classDeclarations, List.of() /* valueDeclarations */);
					pds = List.of(defaultPackageDeclaration);
				} else {
					pds = packageDeclarations;
				}

				result = mdBuilder.build(pds);
			} else { // Top-level module
				ModuleImportEquivalents equiv = new ModuleImportEquivalents();
				List<ModuleImport> moduleImports = List.of();
				Optional<QName> qname = Optional.of(Constants.topLevelModuleQName);

				if (!packageElements.isEmpty()) { // package elements before first package declaration => prepend
													// unnamed package
					AstPackageDeclaration unnamedPackage = new AstPackageDeclaration(reporter, qname,
							packageElements.functiondefinitions, packageElements.classDeclarations, List.of());

					packageDeclarations.addFirst(unnamedPackage);
				}

				result = AstModuleDeclaration.createUnnamed(reporter, equiv, moduleImports, packageDeclarations);
			}

			return result;
		}
	}

	public static class AstModuleDeclarationBuilder {
		private QName qualifiedName;
		private Reporter reporter;
		private AstToken version;
		private ModuleImportEquivalents equivalents;
		private List<ModuleImport> moduleImports;
		private List<AstToken> comments;

		public AstModuleDeclarationBuilder(Reporter reporter, QName qualifiedName, AstToken version,
				ModuleImportEquivalents equivalents, List<ModuleImport> moduleImports, List<AstToken> comments) {
			super();
			this.qualifiedName = qualifiedName;
			this.reporter = reporter;
			this.version = version;
			this.equivalents = equivalents;
			this.moduleImports = moduleImports;
			this.comments = comments;
		}

		public QName getQualifiedName() {
			return qualifiedName;
		}

		public AstModuleDeclaration build(List<AstPackageDeclaration> packageDeclarations) {
			return new AstModuleDeclaration(reporter, Optional.of(qualifiedName), version, equivalents, moduleImports,
					packageDeclarations, comments);
		}
	}

	public static class ModuleDeclarationVisitor extends SParserBaseVisitor<AstModuleDeclarationBuilder> {
		private Reporter reporter;
		private Parsers parsers;
		private CommonTokenStream tokens;

		public ModuleDeclarationVisitor(Reporter reporter, CommonTokenStream tokens) {
			super();
			this.reporter = reporter;
			this.parsers = new Parsers(reporter, tokens);
			this.tokens = tokens;
		}

		@Override
		public AstModuleDeclarationBuilder visitModuleDeclaration(ModuleDeclarationContext ctx) {

			// -- Initial comment(s)
			Token semi = ctx.getStart(); // start = comment on first ModuleDeclaration token ('module')
			int i = semi.getTokenIndex();
			List<Token> cmtChannel = tokens.getHiddenTokensToLeft(i, SLexer.CommentChannel);

			List<AstToken> commentTokens = (cmtChannel == null) ? List.of()
					: cmtChannel.stream().map(token -> new AstToken(token)).toList();
			
			// -- name
			Parsers.QualifiedNameVisitor nameVisitor = parsers.new QualifiedNameVisitor();
			QualifiedName qualifiedName = ctx.qname().accept(nameVisitor);

			// -- version
			AstToken version = new AstToken(ctx.version);

			// -- equivalents
			ModuleImportEquivalents equivalents = new ModuleImportEquivalents();
			ImportEquivalentVisitor equivalentVisitor = new ImportEquivalentVisitor(equivalents);
			ctx.moduleVersionEquivalent().stream().forEach(equivCtxt -> {
				equivCtxt.accept(equivalentVisitor);
			});

			// -- imports
			ModuleImportVisitor moduleImportVisitor = new ModuleDeclarationParser(reporter,
					tokens).new ModuleImportVisitor();
			List<ModuleImport> moduleImports = ctx.children.stream().map(tree -> tree.accept(moduleImportVisitor))
//				.filter(modImport -> modImport!=null)
					.filter(Objects::nonNull).collect(Collectors.toList());

			return new AstModuleDeclarationBuilder(reporter, qualifiedName.toQName(), version, equivalents,
					moduleImports/* , packageDeclarations */
					, commentTokens);
		}
	}
}
