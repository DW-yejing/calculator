# calculator

[简体中文](README.md) | [English](README_EN.md)

## I/O

Input: arithmetic expression string
Output: value(no precision loss)

## Process

1. convert the arithmetic expression into RPN(reverse polish notation), and stored in a stack

2. pop the elements, calculate based on **java.math.BigDecimal**
