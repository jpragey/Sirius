package org.sirius.frontend.ast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.sirius.common.core.QName;
import org.sirius.frontend.ast.AstClassOrInterface.AncestorInfo;

public interface AstClassOrInterface {

	public static class MapOfList<K, T> {
		private HashMap<K, List<T>> map = new HashMap<>();
		private Supplier<List<T>> listSupplier;

		public MapOfList(Supplier<List<T>> listSupplier) {
			super();
			this.listSupplier = listSupplier;
		}
		public MapOfList() {
			this(() -> new ArrayList<T>());
		}
		public void put(K key, T value) {
			List<T> list = map.get(key);
			if(list == null) {
				list = listSupplier.get();
				map.put(key, list);
			}
			list.add(value);
		}

		public HashMap<K, List<T>> getMap() {
			return map;
		}
		public Set<K> keySet() {
			return map.keySet();
		}
		public Collection<List<T>> values() {
			return map.values();
		}
		
		public void forEach(K key, Consumer<T> action) {
			List<T> actual = map.get(key);
			if(actual != null) {
				actual.forEach(action);
			}
		}
		
		public void insert(MapOfList<K, T> other) {
			for(Map.Entry<K, List<T>> e: other.map.entrySet()) {
				K key = e.getKey();
				for(T value: e.getValue()) {
					put(key, value);
				}
			}
		}
		public List<T> get(K key) {
			return map.get(key);
		}
	}

	/** directly implemented interfaces or extended classes */
	public static class AncestorInfo {
		/** Name in declaration ( extends/implements NAME clause)*/
		private AstToken simpleName;
		private Optional<AstClassDeclaration> astClassDecl = Optional.empty();
		public AncestorInfo(AstToken simpleName) {
			super();
			this.simpleName = simpleName;
		}
		public AstToken getSimpleName() {
			return simpleName;
		}
		public Optional<AstClassDeclaration> getAstClassDecl() {
			return astClassDecl;
		}
	}

	
	
	
	
	
	
	
	public List<AstFunctionDeclaration> getFunctionDeclarations();

	public List<AncestorInfo> getAncestors();
	public List<AstClassDeclaration> getInterfaces();

	public default MapOfList<QName, AstFunctionDeclaration> getAllFunctions() {
		MapOfList<QName, AstFunctionDeclaration> map = new MapOfList<>();

		// -- 
		for(AstFunctionDeclaration func : getFunctionDeclarations()) {
			QName fqn = func.getQName();
			map.put(fqn, func);
		}
		
		// -- add ancestor-defined functions
//		for(AncestorInfo ai:  getAncestors()) {
//			ai.astClassDecl.ifPresent(acd -> {
//				MapOfList<QName, AstFunctionDeclaration> amap  = acd.getAllFunctions();
//				map.insert(amap);
//			});
//		}
		for(AstClassDeclaration acd: this.getInterfaces()) {
			MapOfList<QName, AstFunctionDeclaration> amap  = acd.getAllFunctions();
			map.insert(amap);
		}
		
		return map;
	}
	
	
}
