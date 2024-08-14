package com.example.boot.A14;

import org.springframework.cglib.core.Signature;

public class ProxyFastClass {
    static Signature save0 = new Signature("saveSuper", "()V");
    static Signature save1 = new Signature("saveSuper", "(I)V");
    static Signature save2 = new Signature("saveSuper", "(J)V");

    // <p>记录编号，避免反射调用去找方法签名</p>
    // 获取目标方法的编号
    // signature: 方法签名
    public int getIndex(Signature signature) {
        if (save0.equals(signature)) return 0;
        if (save1.equals(signature)) return 1;
        if (save2.equals(signature)) return 2;
        return -1;
    }

    // 根据返回的方法编号，正常调用目标对象的方法
    public Object invoke(int index, Object proxy, Object[] args){
        if (index == 0) {
            ((Proxy) proxy).saveSuper();
            return null;
        } else if (index == 1) {
            ((Proxy) proxy).saveSuper((Integer) args[0]);
            return null;
        } else if (index == 2) {
            ((Proxy) proxy).saveSuper((Long) args[0]);
            return null;
        } else {
            throw new RuntimeException("no method");
        }
    }

    public static void main(String[] args) {
        ProxyFastClass proxyFastClass = new ProxyFastClass();
        int index = proxyFastClass.getIndex(new Signature("save", "(I)V"));
        System.out.println(index);
        proxyFastClass.invoke(index, new Proxy(), new Object[]{100});
    }
}
