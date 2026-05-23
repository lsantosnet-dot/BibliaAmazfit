import { createWidget, widget, align, text_style } from '@zos/ui'
import { openAssetsSync, readSync, statAssetsSync, closeSync, O_RDONLY } from '@zos/fs'
import { px } from '@zos/utils'

function bufferToString(buffer) {
  var uint8 = new Uint8Array(buffer)
  var len = uint8.length
  var result = ''
  var tempCodes = []
  var i = 0
  while (i < len) {
    var c = uint8[i++]
    if (c < 128) {
      tempCodes.push(c)
    } else if (c > 191 && c < 224) {
      if (i < len) {
        var c2 = uint8[i++]
        tempCodes.push(((c & 31) << 6) | (c2 & 63))
      }
    } else if (c > 223 && c < 240) {
      if (i + 1 < len) {
        var c2 = uint8[i++]
        var c3 = uint8[i++]
        tempCodes.push(((c & 15) << 12) | ((c2 & 63) << 6) | (c3 & 63))
      }
    }
    if (tempCodes.length >= 1024) {
      result += String.fromCharCode.apply(null, tempCodes)
      tempCodes = []
    }
  }
  if (tempCodes.length > 0) {
    result += String.fromCharCode.apply(null, tempCodes)
  }
  return result
}

function readAssetJson(path) {
  try {
    var stat = statAssetsSync({ path: path })
    if (!stat) return null
    var fd = openAssetsSync({ path: path, flag: O_RDONLY })
    if (fd < 0) return null
    var buffer = new ArrayBuffer(stat.size)
    var bytesRead = readSync({ fd: fd, buffer: buffer })
    closeSync(fd)
    if (bytesRead > 0) {
      var str = bufferToString(buffer.slice(0, bytesRead))
      return JSON.parse(str)
    }
  } catch (e) {
    console.log('readAssetJson error:', e)
  }
  return null
}

function getTypeId(length) {
  if (length === 0) return 6
  if (length <= 60) return 1
  if (length <= 120) return 2
  if (length <= 200) return 3
  if (length <= 300) return 4
  return 5
}

Page({
  build: function () {
    var appData = getApp()._options.globalData
    var bookId = appData.bookId
    var bookName = appData.bookName
    var chapter = appData.chapter

    console.log('MiniBíblia Leitor - ' + bookName + ' Cap ' + chapter + ' (ID:' + bookId + ')')

    createWidget(widget.TEXT, {
      x: 0,
      y: px(20),
      w: px(480),
      h: px(45),
      text: bookName.toUpperCase() + ' ' + chapter,
      color: 0xFFD700,
      text_size: px(30),
      align_h: align.CENTER_H,
      align_v: align.CENTER_V
    })

    createWidget(widget.FILL_RECT, {
      x: px(160),
      y: px(68),
      w: px(160),
      h: px(2),
      color: 0xFFD700
    })

    var bookData = readAssetJson('bible/' + bookId + '.json')
    if (!bookData) {
      createWidget(widget.TEXT, {
        x: px(40),
        y: px(200),
        w: px(400),
        h: px(80),
        text: 'Erro ao carregar o texto.',
        color: 0xFF0000,
        text_size: px(20),
        align_h: align.CENTER_H
      })
      return
    }

    var verses = bookData.chapters[chapter - 1]
    if (!verses || verses.length === 0) {
      createWidget(widget.TEXT, {
        x: px(40),
        y: px(200),
        w: px(400),
        h: px(80),
        text: 'Capítulo não encontrado.',
        color: 0xFF0000,
        text_size: px(20),
        align_h: align.CENTER_H
      })
      return
    }

    var data_array = []
    data_array.push({ text: '' })

    for (var v = 0; v < verses.length; v++) {
      data_array.push({
        text: (v + 1) + '  ' + verses[v]
      })
    }

    data_array.push({ text: '' })

    var item_config = [
      {
        type_id: 1,
        item_height: px(140),
        text_view: [{ x: px(40), y: px(10), w: px(400), h: px(120), key: 'text', color: 0xFFFFFF, text_size: px(26), text_style: text_style.WRAP, line_space: px(6) }],
        text_view_count: 1
      },
      {
        type_id: 2,
        item_height: px(220),
        text_view: [{ x: px(40), y: px(10), w: px(400), h: px(200), key: 'text', color: 0xFFFFFF, text_size: px(26), text_style: text_style.WRAP, line_space: px(6) }],
        text_view_count: 1
      },
      {
        type_id: 3,
        item_height: px(330),
        text_view: [{ x: px(40), y: px(10), w: px(400), h: px(310), key: 'text', color: 0xFFFFFF, text_size: px(26), text_style: text_style.WRAP, line_space: px(6) }],
        text_view_count: 1
      },
      {
        type_id: 4,
        item_height: px(470),
        text_view: [{ x: px(40), y: px(10), w: px(400), h: px(450), key: 'text', color: 0xFFFFFF, text_size: px(26), text_style: text_style.WRAP, line_space: px(6) }],
        text_view_count: 1
      },
      {
        type_id: 5,
        item_height: px(690),
        text_view: [{ x: px(40), y: px(10), w: px(400), h: px(670), key: 'text', color: 0xFFFFFF, text_size: px(26), text_style: text_style.WRAP, line_space: px(6) }],
        text_view_count: 1
      },
      {
        type_id: 6,
        item_height: px(140),
        text_view: [],
        text_view_count: 0
      }
    ]

    var data_type_config = []
    for (var d = 0; d < data_array.length; d++) {
      var len = data_array[d].text.length
      var tid = getTypeId(len)
      data_type_config.push({ start: d, end: d, type_id: tid })
    }

    createWidget(widget.SCROLL_LIST, {
      x: 0,
      y: px(80),
      w: px(480),
      h: px(400),
      item_space: px(10),
      item_config: item_config,
      item_config_count: item_config.length,
      data_array: data_array,
      data_count: data_array.length,
      data_type_config: data_type_config,
      data_type_config_count: data_type_config.length
    })
  }
})
