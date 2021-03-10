# **Flutter**

> Flutter是谷歌的移动UI框架，可以快速在iOS和Android上构建高质量的原生用户界面。
> Flutter可以与现有的代码一起工作。
> 在全世界，Flutter正在被越来越多的开发者和组织使用，并且Flutter是完全免费、开源的。

- 快速开发：毫秒级的热重载，修改后，您的应用界面会立即更新。使用丰富的、完全可定制的widget在几分钟内构建原生界面。
- 富有表现力和灵活的UI：快速发布聚焦于原生体验的功能。分层的架构允许您完全自定义，从而实现难以置信的快速渲染和富有表现力、灵活的设计。
- 原生性能：Flutter包含了许多核心的widget，如滚动、导航、图标和字体等，这些都可以在iOS和Android上达到原生应用一样的性能。

&nbsp;

## 快速开发

Flutter的热重载可帮助您快速地进行测试、构建UI、添加功能并更快地修复错误。在iOS和Android模拟器或真机上可以在亚秒内重载，并且不会丢失状态。

&nbsp;

## 富有表现力，漂亮的用户界面

使用Flutter内置美丽的Material Design和Cupertino（iOS风格）widget、丰富的motion API、平滑而自然的滑动效果和平台感知，为您的用户带来全新体验。

&nbsp;

## 现代的，响应式框架

使用Flutter的现代、响应式框架，和一系列基础widget，轻松构建您的用户界面。使用功能强大且灵活的API（针对2D、动画、手势、效果等）解决艰难的UI挑战。

```
class CounterState extends State<Counter> {
  int counter = 0;

  void increment() {
    // 告诉Flutter state已经改变, Flutter会调用build()，更新显示
    setState(() {
      counter++;
    });
  }

  Widget build(BuildContext context) {
    // 当 setState 被调用时，这个方法都会重新执行.
    // Flutter 对此方法做了优化，使重新执行变的很快
    // 所以你可以重新构建任何需要更新的东西，而无需分别去修改各个widget
    return new Row(
      children: <Widget>[
        new RaisedButton(
          onPressed: increment,
          child: new Text('Increment'),
        ),
        new Text('Count: $counter'),
      ],
    );
  }
}
```

&nbsp;

## 访问本地功能和SDK

通过平台相关的API、第三方SDK和原生代码让您的应用变得强大易用。
Flutter允许您复用现有的Java、Swift或ObjC代码，访问iOS和Android上的原生系统功能和系统SDK。

```
Future<Null> getBatteryLevel() async {
  var batteryLevel = 'unknown';
  try {
    int result = await methodChannel.invokeMethod('getBatteryLevel');
    batteryLevel = 'Battery level: $result%';
  } on PlatformException {
    batteryLevel = 'Failed to get battery level.';
  }
  setState(() {
    _batteryLevel = batteryLevel;
  });
}
```

&nbsp;

## 统一的应用开发体验

Flutter拥有丰富的工具和库，可以帮助您轻松地同时在iOS和Android系统中实现您的想法和创意。
如果您没有任何移动端开发体验，Flutter是一种轻松快捷的方式来构建漂亮的移动应用程序。
如果您是一位经验丰富的iOS或Android开发人员，则可以使用Flutter作为视图层来混合开发。