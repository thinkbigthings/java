package org.thinkbigthings.demo.records;

public sealed interface Expression permits Expression.IntExp, Expression.AddExp, Expression.SubtractExp {

    int value();

    // "final" here is kind of unnecessary since records are already final
    // but it gets rid of the IDE warning :)
    final record IntExp(int value) implements Expression {
    }

    final record AddExp(Expression left, Expression right) implements Expression {
        public int value() {
            return left.value() + right.value();
        }
    }

    final record SubtractExp(Expression left, Expression right) implements Expression {
        public int value() {
            return left.value() - right.value();
        }
    }
}
