package org.androidannotations.codemodel;

import com.sun.codemodel.JExpressionImpl;
import com.sun.codemodel.JFormatter;
import com.sun.codemodel.JStatement;
import com.sun.codemodel.JVar;

public class JIncrement extends JExpressionImpl implements JStatement {
    JVar var;

    public JIncrement(JVar var) {
        this.var = var;
    }

    public void generate(JFormatter f) {
        f.g(this.var).p("++");
    }

    public void state(JFormatter f) {
        f.g(this).p(';').nl();
    }
}
