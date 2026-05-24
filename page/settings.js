import { createWidget, widget, align, deleteWidget } from '@zos/ui'
import { back } from '@zos/router'
import { writeFileSync, readFileSync } from '@zos/fs'
import { px } from '@zos/utils'

Page({
  build: function () {
    var appData = getApp()._options.globalData
    var currentSize = 26

    try {
      const content = readFileSync({
        path: 'font.txt',
        options: {
          encoding: 'utf8'
        }
      })
      if (content) {
        currentSize = parseInt(content, 10) || appData.fontSize || 26
      } else {
        currentSize = appData.fontSize || 26
      }
    } catch (e) {
      currentSize = appData.fontSize || 26
    }

    // Título
    createWidget(widget.TEXT, {
      x: 0,
      y: px(35),
      w: px(480),
      h: px(40),
      text: 'TAMANHO DA FONTE',
      color: 0xFFD700,
      text_size: px(28),
      align_h: align.CENTER_H,
      align_v: align.CENTER_V
    })

    // Container de Controles
    createWidget(widget.FILL_RECT, {
      x: px(50),
      y: px(95),
      w: px(380),
      h: px(90),
      color: 0x111111,
      radius: px(15)
    })

    // Container de Pré-visualização
    createWidget(widget.FILL_RECT, {
      x: px(50),
      y: px(200),
      w: px(380),
      h: px(140),
      color: 0x1A1A1A,
      radius: px(15)
    })

    // Rótulo da Amostra
    createWidget(widget.TEXT, {
      x: px(60),
      y: px(210),
      w: px(360),
      h: px(30),
      text: 'Amostra de texto:',
      color: 0x888888,
      text_size: px(18),
      align_h: align.CENTER_H
    })

    var sizeTextWidget = null
    var previewWidget = null

    function renderDynamicWidgets() {
      if (sizeTextWidget) {
        try {
          deleteWidget(sizeTextWidget)
        } catch (e) {
          console.log('Erro ao deletar sizeTextWidget:', e)
        }
      }
      if (previewWidget) {
        try {
          deleteWidget(previewWidget)
        } catch (e) {
          console.log('Erro ao deletar previewWidget:', e)
        }
      }

      // Indicador de tamanho
      sizeTextWidget = createWidget(widget.TEXT, {
        x: px(180),
        y: px(100),
        w: px(120),
        h: px(80),
        text: currentSize + ' px',
        color: 0xFFFFFF,
        text_size: px(30),
        align_h: align.CENTER_H,
        align_v: align.CENTER_V
      })

      // Texto de amostra dinâmico
      previewWidget = createWidget(widget.TEXT, {
        x: px(60),
        y: px(245),
        w: px(360),
        h: px(85),
        text: 'Gênesis',
        color: 0xFFFFFF,
        text_size: px(currentSize),
        align_h: align.CENTER_H,
        align_v: align.CENTER_V
      })
    }

    renderDynamicWidgets()

    // Botão de Menos (-)
    createWidget(widget.BUTTON, {
      x: px(65),
      y: px(105),
      w: px(90),
      h: px(70),
      text: '-',
      normal_color: 0x222222,
      press_color: 0xFFD700,
      radius: px(10),
      text_size: px(36),
      color: 0xFFFFFF,
      click_func: function () {
        if (currentSize > 18) {
          currentSize -= 2
          renderDynamicWidgets()
        }
      }
    })

    // Botão de Mais (+)
    createWidget(widget.BUTTON, {
      x: px(325),
      y: px(105),
      w: px(90),
      h: px(70),
      text: '+',
      normal_color: 0x222222,
      press_color: 0xFFD700,
      radius: px(10),
      text_size: px(36),
      color: 0xFFFFFF,
      click_func: function () {
        if (currentSize < 42) {
          currentSize += 2
          renderDynamicWidgets()
        }
      }
    })

    // Botão OK
    createWidget(widget.BUTTON, {
      x: px(140),
      y: px(360),
      w: px(200),
      h: px(70),
      text: 'OK',
      normal_color: 0xFFD700,
      press_color: 0xCCAA00,
      radius: px(15),
      text_size: px(28),
      color: 0x000000,
      click_func: function () {
        appData.fontSize = currentSize
        if (getApp()._options && getApp()._options.globalData) {
          getApp()._options.globalData.fontSize = currentSize
        }
        try {
          writeFileSync({
            path: 'font.txt',
            data: String(currentSize)
          })
          console.log('Tamanho da fonte gravado no arquivo: ' + currentSize)
        } catch (e) {
          console.log('Erro ao gravar no arquivo:', e)
        }
        back()
      }
    })
  }
})
