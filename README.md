# RichView
###### _收集&开发 一些自定义view_
所有的库会本着 _最小依赖原则_ 来编写,尽可能的不去依赖过多的库,完成功能,将业务代码交给用户
### 收录记录:
1.自定了下拉刷新&上拉加载控件,引入了lottie动画库
2.自定义Banner控件: 使用ViewPager2,使用代理的方式完成封装

### 依赖方式

添加maven仓库

```css
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
```

添加依赖

```css
dependencies {
    implementation 'com.github.codeLeejj:RichView:1.0.3'
}
```
## 库的介绍或说明：
I.下拉刷新&上拉加载：
II.Banner：
- 1.滑动间隔时间（interval）不得低于2_000毫秒
- 2.动画持续时间（duration）必须小于或等于滑动间隔时间
##### _使用建议_：
- 1.让 interval==duration ,达到连续动画效果
