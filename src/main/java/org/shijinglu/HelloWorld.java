package org.shijinglu;

public class HelloWorld {

    public String sayHello() {
        return "hello";
    }

    public static void main(String[] args) {
        System.out.println(new HelloWorld().sayHello() + " world");
    }
}
