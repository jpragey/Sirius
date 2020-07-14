package org.sirius.sdk.org.sirius;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.sirius.sdk.tooling.TopLevelClass;

public class TopLevelTest {

	
	
	@Test
	public void detectTopLevelFunctions() {
		List<Class<?>> allSdkClasses = Arrays.asList(TopLevel.class);
		
		for(Class<?> clss: allSdkClasses) {
			
			TopLevelClass topLevelAnno = clss.getDeclaredAnnotation(TopLevelClass.class);
			if(topLevelAnno != null) {
				System.out.println("- Top-level class: " + clss + ", anno: " + topLevelAnno);
			}
		}
		
		
//		Arrays.asList(topLevelClass.getMethods()).stream()
//			.filter(method -> )
//			.forEach(method -> System.out.println(method.getName()));
	}
}
