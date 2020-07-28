# calculator

[简体中文](README.md) | [English](README_EN.md)

## 输入输出

输入： 算数表达式
输出： 无损失结果值

## 过程

1. 解析算数表达式为后缀表达式，以栈结构存储

2. 弹出数据和运算符，基于java.math.BigDecimal，实现无精度损失计算
