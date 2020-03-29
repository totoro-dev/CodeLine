# CodeLine 代码行统计
这是一个可以方便开发人员统计项目代码行数的根据，统计的行数不包括空行和注释的内容，统计结果较为精准。

其中对个别的代码编辑器（IDEA、Android Studio...）有较好的兼容，体现在排除".idea"、"build"、"target"、"out"等文件夹自动生成的文件对结果的影响。不过任何IDE构建的项目都能够被统计，只是这里希望有其它IDE编程经验的开发人员一起来丰富这个工具。

## 基本使用

![](https://raw.githubusercontent.com/totoro-dev/CodeLine/master/img/show1.png)

- 在输入框输入（粘贴）想要统计的项目的路径。
- 使用路径自动补全，进入具体的文件夹进行统计。
- 选择想要统计代码行的文件夹类型。
