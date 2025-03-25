# Krupp

----

## 介绍
Java公共工具类库

## 项目命名
krupp\[krʌp\][克虏伯公司][krupp]为德国工业巨头，历史悠久。曾几何时， [克虏伯大炮][cannon]几乎就是中国海防的代名词。

krupp的[Bagger288][bagger288]是世界上最大的挖掘机，可能也是世界上最大的机械。它的长度有195米，高度67米，工作重量7800吨，行走机构的12条履带采用前8后4的布置方式，底盘的宽度是31米。是人类工业的杰作。

项目名为krupp正是因为这个庞然大物（叫Bagger听起来容易有歧义。。。）人家挖煤挖的如此大气磅礴、雄伟壮观，咱们搬砖的也应该向他们学习！

## 如何使用
1. 添加Maven私服  
    nexus.bl-ai.com
2. 项目中添加Maven依赖
```
<dependency>
  <groupId>ai.bailian</groupId>
  <artifactId>krupp</artifactId>
  <version>[0,)</version>
</dependency>
```

## 开发规范、注意事项
1. 开发新功能前，需检查知名度高且活跃开源项目是否有类似功能，避免重复造轮子
2. 注释清晰简要，符合[Javadoc注释规范][javadoc]
3. API向下兼容。废弃的接口使用@deprecated进行标明，不可直接删除
4. 每次commit需要是一个完整的功能实现，并写明更新内容
5. 所有对外API都需要在[Wiki][wiki]中有文档介绍用法、和适用场景
6. 尽量减少对版本兼容性差的第三方库的引用（例如guava），容易引起依赖版本冲突
7. 对于一些使用面较窄的第三方库（例如图像处理、csv）,引用时Option设置为ture，并在相关调用类的[Wiki][wiki]中写明需要额外引用

## 版本号规范
版本号格式为`主版本号.子版本号.修正版本号`

`主版本号`: 当有新的工具类发布时，主版本号会更新。  
`子版本号`: 当工具类下有新的API发布时，子版本号会更新。  
`修正版本号`: 当发生bug修复时，修正版本号会更新。

## 代码提交、项目发布流程
1. 请注意，每一个API均要求有足够多的单元测试，没有单元测试`禁止提交代码`
2. 将代码修改push到`develop`分支
3. 向master分支提交PR，设置项目管理者参与review
4. 项目管理者review后会提出修改建议，直至无异议并合并代码
5. 项目管理者在jenkins中执行`krupp`-`Perform Maven Release`进行发布，设置版本号时要注意需要符合`版本号规范`



[wiki]: https://gitlab.bailian-ai.com/basedata/krupp/wikis
[javadoc]: https://en.wikipedia.org/wiki/Javadoc
[krupp]: http://baike.baidu.com/view/5360734.htm
[cannon]: http://baike.baidu.com/view/2017424.htm
[bagger288]: https://en.wikipedia.org/wiki/Bagger_288