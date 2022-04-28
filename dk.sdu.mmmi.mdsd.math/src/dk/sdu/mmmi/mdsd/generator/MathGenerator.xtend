package dk.sdu.mmmi.mdsd.generator

import dk.sdu.mmmi.mdsd.math.Div
import dk.sdu.mmmi.mdsd.math.LetBinding
import dk.sdu.mmmi.mdsd.math.MathExp
import dk.sdu.mmmi.mdsd.math.MathNumber
import dk.sdu.mmmi.mdsd.math.Minus
import dk.sdu.mmmi.mdsd.math.Mult
import dk.sdu.mmmi.mdsd.math.Plus
import dk.sdu.mmmi.mdsd.math.VarBinding
import dk.sdu.mmmi.mdsd.math.VariableUse
import dk.sdu.mmmi.mdsd.math.Parenthesis
import java.util.Map
import javax.swing.JOptionPane
import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.xtext.generator.AbstractGenerator
import org.eclipse.xtext.generator.IFileSystemAccess2
import org.eclipse.xtext.generator.IGeneratorContext
import java.io.File
import dk.sdu.mmmi.mdsd.math.Expression
import java.util.HashMap

/**
 * Generates code from your model files on save.
 * 
 * See https://www.eclipse.org/Xtext/documentation/303_runtime_concepts.html#code-generation
 */
class MathGenerator extends AbstractGenerator {
	
	static Map<String, String> variables;
	
	override void doGenerate(Resource resource, IFileSystemAccess2 fsa, IGeneratorContext context) {
		val math = resource.allContents.filter(MathExp).next
		
		val className = math.name;
		val classText = generateJavaFile(math);
		
		new File("math_expression").mkdirs()
        fsa.generateFile("math_expression/" + 
            className +  ".java", classText)
	}
	
	
	def generateJavaFile (MathExp mathExp) {
		return '''
		package math_expression;
		
		public class «mathExp.name» {
			«mathExp.generateAttributes»
			«mathExp.generateComputeFunctions»
		}
		'''
	}


	def generateAttributes (MathExp mathExp) {
		return '''
		«FOR a : mathExp.variables»
		public int «a.name»;
		«ENDFOR»
		'''
	}
	
	def generateComputeFunctions (MathExp mathExp) {
		mathExp.compute()
		return '''
		public void compute() {
			«FOR a : mathExp.variables»
			«a.name» = «a.expression.generateExpression»;
			«ENDFOR»
		}
		'''
	}

	def static compute(MathExp mathExp){
		variables = new HashMap()
		for(vars : mathExp.variables)
			vars.expression.generateExpression()
		return variables
	}
	
	def static String generateExpression(Expression exp) {
		switch exp {
			Plus: exp.left.generateExpression() + " + " + exp.right.generateExpression()
			Minus: exp.left.generateExpression() + " - " + exp.right.generateExpression()
			Mult: exp.left.generateExpression() + " * " + exp.right.generateExpression()
			Div: exp.left.generateExpression() + " / " + exp.right.generateExpression()
			Parenthesis: "(" + exp.exp.generateExpression() + ")"
			LetBinding: "(" + exp.body.generateExpression() + ")"
			VariableUse: "(" + exp.ref.handleBinding() + ")"
			MathNumber: exp.value.toString()
		}
		
	}
	
	def static generateVarBinding(VarBinding varB){
		variables.put(varB.name, varB.expression.generateExpression())
		return variables.get(varB.name)
	}
	
	def static dispatch String handleBinding(LetBinding letB){
		"(" + letB.binding.generateExpression() + ")"
	}
	
	def static dispatch String handleBinding(VarBinding varB){
		if (!variables.containsKey(varB.name)) 
			varB.generateVarBinding()
		return variables.get(varB.name)
	}
		
	def void displayPanel(Map<String, Integer> result) {
		var resultString = ""
		for (entry : result.entrySet()) {
         	resultString += "var " + entry.getKey() + " = " + entry.getValue() + "\n"
        }
		
		JOptionPane.showMessageDialog(null, resultString ,"Math Language", JOptionPane.INFORMATION_MESSAGE)
	}
	
}