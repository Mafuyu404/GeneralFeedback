## 通用意见反馈

本模组提供了便捷的图形界面和可选的KJS接口，用于玩家在游戏中实时向开发者发送意见反馈。

![img](https://resource-api.xyeidc.com/client/pics/6c84d2ce)

意见反馈分为全局和局部，全局意见反馈提供给整合包或服务器使用，配置非常简单。

局部意见反馈则可以是单个模组使用，共用一套HUD但互不干涉。

默认情况下全局意见反馈的入口会放置在生存模式物品栏中、暂停菜单中和死亡菜单中，可在配置文件中隐藏。

![img](https://resource-api.xyeidc.com/client/pics/527de612)

对于普通玩家，只需要正常下载本模组并使用即可。

对于开发者，自定义配置非常简单，请参考开发者手册使用。

本模组默认使用维格云作为信息收集后台，预览如下：

![img](https://resource-api.xyeidc.com/client/pics/41f274fb)

此外，本模组也支持：

- 自定义打开意见反馈的时机，如在玩家跳跃时打开意见反馈界面；
- 自定义发送额外信息（依赖于KJS），如连同玩家物品栏信息一同发送；
- 自定义第三方api调用，若有更好的替代品，可不局限于维格云。


## 开发者手册

添加完模组后，什么都不会发生。

### 维格云

首先你需要注册一个维格云账号。

链接：https://vika.cn/workbench

接着，你需要创建自己的意见收集表，并将表头设置成和我一样的形式。

![img](https://resource-api.xyeidc.com/client/pics/41f274fb)

索引和日期是可以设置自动填充的，不过非必要，可以不设置。

接着点击右上角的API，在弹出的半窗口中勾选“显示API Token”，然后根据指引创建自己的token。

确认勾选该选项，并转到“Get 获取”。

![img](https://resource-api.xyeidc.com/client/pics/a3406e95)

需要用到的是curl后的链接和Bearer后的token。

### 全局配置

打开游戏目录/config/GeneralFeedback文件夹，创建一个example.json文件：

```json
{
  "id": "default",
  "title": "意见反馈界面的标题，如XXX整合包",
  "placeholder": "这里是占位符！",
  "token": "填Bearer后的token",
  "url": "填curl后的链接"
}
```

id为default时，这个配置就会成为全局配置，默认情况下，玩家可以通过生存模式物品栏中、暂停菜单中和死亡菜单中的按钮来进入意见反馈界面。

title和placeholder都支持翻译键，用的是Component.translatable。

填好后使用/reload重载即可。

通常流程到此就结束了，如果你有进一步的需求，请往后看。

### 局部配置

当然，你可以一码归一码，创建多个意见收集表，收集不同方面的意见。

也可能只是嫌弃我那几个按钮，认为有更好的时机弹出意见反馈界面。

又或者，你其实是个模组作者，只是需要收集关于你模组的意见反馈，那么建议采用JarJar的形式。

如果你不会，就在build.gradle里的dependencies部分加一段：

```
jarJar("curse.maven:generalfeedback-xxxxxx:xxxxxxx"){
    jarJar.ranged(it, '[0.0.0,100.0.0)')
}
```

接着，你只需要创建多个json配置并在合适的时机调用他们就好。

```javascript
let FeedbackUtils = Java.loadClass("com.sighs.generalfeedback.utils.FeedbackUtils");
FeedbackUtils.openFeedbackScreenOf(id);
```

id就是json配置中的id字段，调用这个方法就能快速打开对应的意见反馈界面。

### 提交事件

```javascript
// client_scripts
FeedbackEvents.onSubmit(event => {
    let form = event.getForm();
    form.feedback = Client.player.mainHandItem.id + form.feedback;
    event.setForm(form);
});
```

这个事件用于在数据即将发送前修改内容发送内容，在例子中，原文的开头加上了玩家主手物品id。

你可以通过KJS加上任何你想要的信息，如整合包版本或玩家游玩进度。

如果你不想用维格云，有其它能用的网站或者自己搭了一个，可以像这样写：

```javascript
// client_scripts
FeedbackEvents.onSubmit(event => {
    let HttpUtils = Java.loadClass("com.sighs.generalfeedback.utils.HttpUtils");
    HttpUtils.fetch();
    event.setCanceled(true);
});
```

这个HttpUtils比较敏感，所以没写KJS兼容，需要自己加载。

你有这个需求，我就当你知道如何调用api了，跟着ProbeJS的补全写fetch方法就行了。