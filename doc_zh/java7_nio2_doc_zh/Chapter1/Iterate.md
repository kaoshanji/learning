# 遍历Path元素


由于Path类实现了Iterable接口，因此您可以获得一个对象，该对象使您能够遍历路径中的元素。 

您可以使用显式迭代器或foreach进行迭代该循环返回每次迭代的Path对象。 

以下是一个示例：

```Java
Path path = Paths.get("C:", "rafaelnadal/tournaments/2009", "BNP.txt");
for (Path name : path) {
    System.out.println(name);
}
```

这将输出从最接近根的元素，如下所示：

```base
rafaelnadal
tournaments
2009
BNP.txt
```

----
