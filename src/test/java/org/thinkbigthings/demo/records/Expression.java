package org.thinkbigthings.demo.records;

sealed interface Expression {

    int value();

    record IntExp(int value) implements Expression {
    }

    record AddExp(Expression left, Expression right) implements Expression {
        public int value() {
            return left.value() + right.value();
        }
    }

    record SubtractExp(Expression left, Expression right) implements Expression {
        public int value() {
            return left.value() - right.value();
        }
    }
}
