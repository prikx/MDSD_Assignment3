package dk.sdu.mmmi.mdsd.generator;

import com.google.common.collect.Iterators;
import dk.sdu.mmmi.mdsd.math.Binding;
import dk.sdu.mmmi.mdsd.math.Div;
import dk.sdu.mmmi.mdsd.math.Expression;
import dk.sdu.mmmi.mdsd.math.LetBinding;
import dk.sdu.mmmi.mdsd.math.MathExp;
import dk.sdu.mmmi.mdsd.math.MathNumber;
import dk.sdu.mmmi.mdsd.math.Minus;
import dk.sdu.mmmi.mdsd.math.Mult;
import dk.sdu.mmmi.mdsd.math.Parenthesis;
import dk.sdu.mmmi.mdsd.math.Plus;
import dk.sdu.mmmi.mdsd.math.VarBinding;
import dk.sdu.mmmi.mdsd.math.VariableUse;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.swing.JOptionPane;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.generator.AbstractGenerator;
import org.eclipse.xtext.generator.IFileSystemAccess2;
import org.eclipse.xtext.generator.IGeneratorContext;

/**
 * Generates code from your model files on save.
 * 
 * See https://www.eclipse.org/Xtext/documentation/303_runtime_concepts.html#code-generation
 */
@SuppressWarnings("all")
public class MathGenerator extends AbstractGenerator {
  private static Map<String, String> variables;
  
  @Override
  public void doGenerate(final Resource resource, final IFileSystemAccess2 fsa, final IGeneratorContext context) {
    final MathExp math = Iterators.<MathExp>filter(resource.getAllContents(), MathExp.class).next();
    final String className = math.getName();
    final String classText = this.generateJavaFile(math);
    new File("math_expression").mkdirs();
    fsa.generateFile((("math_expression/" + className) + ".java"), classText);
  }
  
  public String generateJavaFile(final MathExp mathExp) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("package math_expression;");
    _builder.newLine();
    _builder.newLine();
    _builder.append("public class ");
    String _name = mathExp.getName();
    _builder.append(_name);
    _builder.append(" {");
    _builder.newLineIfNotEmpty();
    _builder.append("\t");
    String _generateAttributes = this.generateAttributes(mathExp);
    _builder.append(_generateAttributes, "\t");
    _builder.newLineIfNotEmpty();
    _builder.append("\t");
    String _generateComputeFunctions = this.generateComputeFunctions(mathExp);
    _builder.append(_generateComputeFunctions, "\t");
    _builder.newLineIfNotEmpty();
    _builder.append("}");
    _builder.newLine();
    return _builder.toString();
  }
  
  public String generateAttributes(final MathExp mathExp) {
    StringConcatenation _builder = new StringConcatenation();
    {
      EList<VarBinding> _variables = mathExp.getVariables();
      for(final VarBinding a : _variables) {
        _builder.append("public int ");
        String _name = a.getName();
        _builder.append(_name);
        _builder.append(";");
        _builder.newLineIfNotEmpty();
      }
    }
    return _builder.toString();
  }
  
  public String generateComputeFunctions(final MathExp mathExp) {
    MathGenerator.compute(mathExp);
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("public void compute() {");
    _builder.newLine();
    {
      EList<VarBinding> _variables = mathExp.getVariables();
      for(final VarBinding a : _variables) {
        _builder.append("\t");
        String _name = a.getName();
        _builder.append(_name, "\t");
        _builder.append(" = ");
        String _generateExpression = MathGenerator.generateExpression(a.getExpression());
        _builder.append(_generateExpression, "\t");
        _builder.append(";");
        _builder.newLineIfNotEmpty();
      }
    }
    _builder.append("}");
    _builder.newLine();
    return _builder.toString();
  }
  
  public static Map<String, String> compute(final MathExp mathExp) {
    HashMap<String, String> _hashMap = new HashMap<String, String>();
    MathGenerator.variables = _hashMap;
    EList<VarBinding> _variables = mathExp.getVariables();
    for (final VarBinding vars : _variables) {
      MathGenerator.generateExpression(vars.getExpression());
    }
    return MathGenerator.variables;
  }
  
  public static String generateExpression(final Expression exp) {
    String _switchResult = null;
    boolean _matched = false;
    if (exp instanceof Plus) {
      _matched=true;
      String _generateExpression = MathGenerator.generateExpression(((Plus)exp).getLeft());
      String _plus = (_generateExpression + " + ");
      String _generateExpression_1 = MathGenerator.generateExpression(((Plus)exp).getRight());
      _switchResult = (_plus + _generateExpression_1);
    }
    if (!_matched) {
      if (exp instanceof Minus) {
        _matched=true;
        String _generateExpression = MathGenerator.generateExpression(((Minus)exp).getLeft());
        String _plus = (_generateExpression + " - ");
        String _generateExpression_1 = MathGenerator.generateExpression(((Minus)exp).getRight());
        _switchResult = (_plus + _generateExpression_1);
      }
    }
    if (!_matched) {
      if (exp instanceof Mult) {
        _matched=true;
        String _generateExpression = MathGenerator.generateExpression(((Mult)exp).getLeft());
        String _plus = (_generateExpression + " * ");
        String _generateExpression_1 = MathGenerator.generateExpression(((Mult)exp).getRight());
        _switchResult = (_plus + _generateExpression_1);
      }
    }
    if (!_matched) {
      if (exp instanceof Div) {
        _matched=true;
        String _generateExpression = MathGenerator.generateExpression(((Div)exp).getLeft());
        String _plus = (_generateExpression + " / ");
        String _generateExpression_1 = MathGenerator.generateExpression(((Div)exp).getRight());
        _switchResult = (_plus + _generateExpression_1);
      }
    }
    if (!_matched) {
      if (exp instanceof Parenthesis) {
        _matched=true;
        String _generateExpression = MathGenerator.generateExpression(((Parenthesis)exp).getExp());
        String _plus = ("(" + _generateExpression);
        _switchResult = (_plus + ")");
      }
    }
    if (!_matched) {
      if (exp instanceof LetBinding) {
        _matched=true;
        String _generateExpression = MathGenerator.generateExpression(((LetBinding)exp).getBody());
        String _plus = ("(" + _generateExpression);
        _switchResult = (_plus + ")");
      }
    }
    if (!_matched) {
      if (exp instanceof VariableUse) {
        _matched=true;
        String _handleBinding = MathGenerator.handleBinding(((VariableUse)exp).getRef());
        String _plus = ("(" + _handleBinding);
        _switchResult = (_plus + ")");
      }
    }
    if (!_matched) {
      if (exp instanceof MathNumber) {
        _matched=true;
        _switchResult = Integer.valueOf(((MathNumber)exp).getValue()).toString();
      }
    }
    return _switchResult;
  }
  
  public static String generateVarBinding(final VarBinding varB) {
    MathGenerator.variables.put(varB.getName(), MathGenerator.generateExpression(varB.getExpression()));
    return MathGenerator.variables.get(varB.getName());
  }
  
  protected static String _handleBinding(final LetBinding letB) {
    String _generateExpression = MathGenerator.generateExpression(letB.getBinding());
    String _plus = ("(" + _generateExpression);
    return (_plus + ")");
  }
  
  protected static String _handleBinding(final VarBinding varB) {
    boolean _containsKey = MathGenerator.variables.containsKey(varB.getName());
    boolean _not = (!_containsKey);
    if (_not) {
      MathGenerator.generateVarBinding(varB);
    }
    return MathGenerator.variables.get(varB.getName());
  }
  
  public void displayPanel(final Map<String, Integer> result) {
    String resultString = "";
    Set<Map.Entry<String, Integer>> _entrySet = result.entrySet();
    for (final Map.Entry<String, Integer> entry : _entrySet) {
      String _resultString = resultString;
      String _key = entry.getKey();
      String _plus = ("var " + _key);
      String _plus_1 = (_plus + " = ");
      Integer _value = entry.getValue();
      String _plus_2 = (_plus_1 + _value);
      String _plus_3 = (_plus_2 + "\n");
      resultString = (_resultString + _plus_3);
    }
    JOptionPane.showMessageDialog(null, resultString, "Math Language", JOptionPane.INFORMATION_MESSAGE);
  }
  
  public static String handleBinding(final Binding letB) {
    if (letB instanceof LetBinding) {
      return _handleBinding((LetBinding)letB);
    } else if (letB instanceof VarBinding) {
      return _handleBinding((VarBinding)letB);
    } else {
      throw new IllegalArgumentException("Unhandled parameter types: " +
        Arrays.<Object>asList(letB).toString());
    }
  }
}
