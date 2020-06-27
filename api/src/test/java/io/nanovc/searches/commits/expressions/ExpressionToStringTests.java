package io.nanovc.searches.commits.expressions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests that we get meaningful toString values for expressions.
 */
public class ExpressionToStringTests
{
    @Test
    public void constantInteger()
    {
        ConstantExpression<Integer> constant = ConstantExpression.of(Integer.class, 42);
        assertEquals("42:Integer", constant.toString());
        assertEquals("Integer", constant.getReturnType().toString());
    }

    @Test
    public void constantBoolean()
    {
        ConstantExpression<Boolean> constant = ConstantExpression.of(Boolean.class, true);
        assertEquals("true:Boolean", constant.toString());
        assertEquals("Boolean", constant.getReturnType().toString());
    }

    @Test
    public void logicalConstantTrue()
    {
        LogicalConstantExpression constant = LogicalConstantExpression.True();
        assertEquals("true:Boolean", constant.toString());
        assertEquals("Boolean", constant.getReturnType().toString());
    }

    @Test
    public void logicalConstantFalse()
    {
        LogicalConstantExpression constant = LogicalConstantExpression.False();
        assertEquals("false:Boolean", constant.toString());
        assertEquals("Boolean", constant.getReturnType().toString());
    }

    @Test
    public void logicalConstantValue()
    {
        LogicalConstantExpression constant = LogicalConstantExpression.of(true);
        assertEquals("true:Boolean", constant.toString());
        assertEquals("Boolean", constant.getReturnType().toString());
    }

    @Test
    public void notTrue()
    {
        NotExpression expression = LogicalConstantExpression.of(true).Not();
        assertEquals("!true:Boolean", expression.toString());
        assertEquals("Boolean", expression.getReturnType().toString());
    }

    @Test
    public void trueAndFalse()
    {
        AndExpression expression = LogicalConstantExpression.True().And(LogicalConstantExpression.False());
        assertEquals("(true:Boolean AND false:Boolean)", expression.toString());
        assertEquals("Boolean", expression.getReturnType().toString());
    }

    @Test
    public void notTrueAndFalse()
    {
        NotExpression expression = LogicalConstantExpression.True().And(LogicalConstantExpression.False()).Not();
        assertEquals("!(true:Boolean AND false:Boolean)", expression.toString());
        assertEquals("Boolean", expression.getReturnType().toString());
    }

    @Test
    public void trueOrFalse()
    {
        OrExpression expression = LogicalConstantExpression.True().Or(LogicalConstantExpression.False());
        assertEquals("(true:Boolean OR false:Boolean)", expression.toString());
        assertEquals("Boolean", expression.getReturnType().toString());
    }

    @Test
    public void notTrueOrFalse()
    {
        NotExpression expression = LogicalConstantExpression.True().Or(LogicalConstantExpression.False()).Not();
        assertEquals("!(true:Boolean OR false:Boolean)", expression.toString());
        assertEquals("Boolean", expression.getReturnType().toString());
    }

    @Test
    public void integerEqualsInteger()
    {
        EqualExpression<Integer> expression = ConstantExpression.of(Integer.class, 42).Equals(ConstantExpression.of(Integer.class, 42));
        assertEquals("(42:Integer==42:Integer)", expression.toString());
        assertEquals("Boolean", expression.getReturnType().toString());
    }

    @Test
    public void integerEqualsConstant()
    {
        EqualExpression<Integer> expression = ConstantExpression.of(Integer.class, 42).EqualsConstant(42);
        assertEquals("(42:Integer==42:Integer)", expression.toString());
        assertEquals("Boolean", expression.getReturnType().toString());
    }

    @Test
    public void integerNotEqualsInteger()
    {
        NotEqualExpression<Integer> expression = ConstantExpression.of(Integer.class, 42).NotEquals(ConstantExpression.of(Integer.class, 43));
        assertEquals("(42:Integer!=43:Integer)", expression.toString());
        assertEquals("Boolean", expression.getReturnType().toString());
    }

    @Test
    public void integerNotEqualsConstant()
    {
        NotEqualExpression<Integer> expression = ConstantExpression.of(Integer.class, 42).NotEqualsConstant(43);
        assertEquals("(42:Integer!=43:Integer)", expression.toString());
        assertEquals("Boolean", expression.getReturnType().toString());
    }

    @Test
    public void allRepoCommits()
    {
        AllRepoCommitsExpression expression = AllRepoCommitsExpression.allRepoCommits();
        assertEquals("[All Repo Commits]", expression.toString());
        assertEquals("List<CommitAPI>", expression.getReturnType().toString());
    }

    @Test
    public void tipOfAllRepoCommits()
    {
        TipOfExpression expression = AllRepoCommitsExpression.allRepoCommits().tip();
        assertEquals("tipOf([All Repo Commits])", expression.toString());
        assertEquals("CommitAPI", expression.getReturnType().toString());
    }

    @Test
    public void branchCommits()
    {
        BranchCommitsExpression expression = BranchCommitsExpression.of("master");
        assertEquals("[Commits for branch: master]", expression.toString());
        assertEquals("List<CommitAPI>", expression.getReturnType().toString());
    }

    @Test
    public void tipOfBranchCommits()
    {
        TipOfExpression expression = BranchCommitsExpression.of("master").tip();
        assertEquals("tipOf([Commits for branch: master])", expression.toString());
        assertEquals("CommitAPI", expression.getReturnType().toString());
    }
}
