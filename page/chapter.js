import { createWidget, widget, align } from '@zos/ui'
import { push } from '@zos/router'
import { setScrollMode, SCROLL_MODE_FREE } from '@zos/page'
import { px } from '@zos/utils'

Page({
  build: function () {
    var appData = getApp()._options.globalData
    var bookId = appData.bookId
    var bookName = appData.bookName
    var chaptersCount = appData.chaptersCount

    console.log('MiniBíblia Capítulos - Livro: ' + bookName + ' ID: ' + bookId + ' Caps: ' + chaptersCount)

    createWidget(widget.TEXT, {
      x: 0,
      y: px(22),
      w: px(480),
      h: px(46),
      text: bookName.toUpperCase(),
      color: 0xFFD700,
      text_size: px(32),
      align_h: align.CENTER_H,
      align_v: align.CENTER_V
    })

    createWidget(widget.TEXT, {
      x: 0,
      y: px(70),
      w: px(480),
      h: px(30),
      text: 'Selecione o Capítulo',
      color: 0x888888,
      text_size: px(20),
      align_h: align.CENTER_H,
      align_v: align.CENTER_V
    })

    var colsCount = 3
    var rowsCount = Math.ceil(chaptersCount / colsCount)
    var contentHeight = px(120 + rowsCount * 88 + 85)

    setScrollMode({
      mode: SCROLL_MODE_FREE,
      options: {
        height: contentHeight
      }
    })

    for (var i = 0; i < chaptersCount; i++) {
      var chapterNum = i + 1
      var col = i % colsCount
      var row = Math.floor(i / colsCount)
      var xPos = px(63 + col * 125)
      var yPos = px(120 + row * 88)

      createWidget(widget.BUTTON, {
        x: xPos,
        y: yPos,
        w: px(105),
        h: px(75),
        text: String(chapterNum),
        normal_color: 0x151515,
        press_color: 0xFFD700,
        radius: px(12),
        text_size: px(28),
        color: 0xFFFFFF,
        click_func: (function (num) {
          return function () {
            console.log('Capítulo clicado: ' + num)
            appData.chapter = num
            push({ url: 'page/reader' })
          }
        })(chapterNum)
      })
    }
  }
})
