import { createWidget, widget, align } from '@zos/ui'
import { push } from '@zos/router'
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

Page({
  build: function () {
    var appData = getApp()._options.globalData

    createWidget(widget.TEXT, {
      x: 0,
      y: px(22),
      w: px(480),
      h: px(48),
      text: 'MINIBÍBLIA',
      color: 0xFFD700,
      text_size: px(32),
      align_h: align.CENTER_H,
      align_v: align.CENTER_V
    })

    createWidget(widget.FILL_RECT, {
      x: px(140),
      y: px(76),
      w: px(200),
      h: px(2),
      color: 0xFFD700
    })

    var books = readAssetJson('books.json')
    if (!books) {
      createWidget(widget.TEXT, {
        x: px(40),
        y: px(200),
        w: px(400),
        h: px(80),
        text: 'Erro ao carregar a base de dados.',
        color: 0xFF0000,
        text_size: px(20),
        align_h: align.CENTER_H
      })
      return
    }

    var data_array = []

    data_array.push({ name: '', chapters_text: '', _bookId: -1, _chapters: 0 })

    for (var b = 0; b < books.length; b++) {
      data_array.push({
        name: books[b].name,
        chapters_text: books[b].chapters + ' Cap.',
        _bookId: books[b].id,
        _chapters: books[b].chapters
      })
    }

    // Botão de Ajustar Fonte no final da lista
    data_array.push({
      name: '⚙️ AJUSTAR FONTE',
      chapters_text: '',
      _bookId: -2,
      _chapters: 0
    })

    data_array.push({ name: '', chapters_text: '', _bookId: -1, _chapters: 0 })

    var item_config = [
      {
        type_id: 1,
        item_height: px(90),
        item_bg_color: 0x111111,
        item_bg_radius: px(12),
        text_view: [
          { x: px(25), y: px(25), w: px(240), h: px(40), key: 'name', color: 0xFFFFFF, text_size: px(28) },
          { x: px(275), y: px(29), w: px(120), h: px(32), key: 'chapters_text', color: 0xAAAAAA, text_size: px(22), align_h: align.RIGHT }
        ],
        text_view_count: 2
      },
      {
        type_id: 2,
        item_height: px(80),
        text_view: [],
        text_view_count: 0
      },
      {
        type_id: 3,
        item_height: px(90),
        item_bg_color: 0x222222,
        item_bg_radius: px(12),
        text_view: [
          { x: px(20), y: px(25), w: px(380), h: px(40), key: 'name', color: 0xFFD700, text_size: px(26), align_h: align.CENTER_H }
        ],
        text_view_count: 1
      }
    ]

    var total_items = data_array.length
    var data_type_config = [
      { start: 0, end: 0, type_id: 2 },
      { start: 1, end: total_items - 3, type_id: 1 },
      { start: total_items - 2, end: total_items - 2, type_id: 3 },
      { start: total_items - 1, end: total_items - 1, type_id: 2 }
    ]

    createWidget(widget.SCROLL_LIST, {
      x: px(30),
      y: px(85),
      w: px(420),
      h: px(380),
      item_space: px(8),
      item_config: item_config,
      item_config_count: item_config.length,
      data_array: data_array,
      data_count: data_array.length,
      data_type_config: data_type_config,
      data_type_config_count: data_type_config.length,
      item_click_func: function (list, index) {
        if (index === 0 || index === total_items - 1) return

        var selected = data_array[index]
        if (selected._bookId === -2) {
          console.log('Roteando para tela de configurações')
          push({ url: 'page/settings' })
          return
        }

        console.log('Livro selecionado: ' + selected.name + ' ID:' + selected._bookId)

        appData.bookId = selected._bookId
        appData.bookName = selected.name
        appData.chaptersCount = selected._chapters

        push({ url: 'page/chapter' })
      }
    })
  }
})
