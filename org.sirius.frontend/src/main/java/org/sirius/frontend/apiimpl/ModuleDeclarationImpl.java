package org.sirius.frontend.apiimpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.sirius.common.core.QName;
import org.sirius.frontend.api.ModuleDeclaration;
import org.sirius.frontend.api.PackageDeclaration;
import org.sirius.frontend.core.PhysicalPath;


public record ModuleDeclarationImpl(
		Optional<QName> qName, 
		String version, 
		PhysicalPath physicalPath, 
		List<PackageDeclaration> packageDeclarations) 
	implements ModuleDeclaration 
{
	public static class Builder {
		Optional<QName> qName = Optional.empty(); 
		String version = "";
		PhysicalPath physicalPath = PhysicalPath.empty;
		List<PackageDeclaration> packageDeclarations = new ArrayList<>();
		
		public Builder qName(Optional<QName> qName) {this.qName = qName; return this;}
		public Builder qName(QName qName) {this.qName = Optional.of(qName); return this;}
		public Builder qName(String... elements) {return qName(QName.of(elements));}
		public Builder version(String version) {this.version = version; return this;}
		public Builder physicalPath(PhysicalPath physicalPath) {this.physicalPath = physicalPath; return this;}

		public Builder packageDeclarations(List<PackageDeclaration> packageDeclarations) {
			this.packageDeclarations = packageDeclarations;
			return this;
		}

		public Builder addPackageDeclarations(PackageDeclaration packageDeclaration) {
			this.packageDeclarations.add(packageDeclaration);
			return this;
		}
		
		public ModuleDeclarationImpl create() {
			return new ModuleDeclarationImpl(
					qName, 
					version, 
					physicalPath, 
					packageDeclarations);
		}
	}
	
	@Override
	public String toString() {
		return "\"" + qName().toString() + "\"";
	}

}
