import { readFileSync } from '@zos/fs'

App({
  onCreate() {
    console.log('MiniBíblia: App criado')
    try {
      const content = readFileSync({
        path: 'font.txt',
        options: {
          encoding: 'utf8'
        }
      })
      if (content) {
        const savedSize = parseInt(content, 10)
        if (savedSize) {
          this.globalData.fontSize = savedSize
          if (this._options && this._options.globalData) {
            this._options.globalData.fontSize = savedSize
          }
        }
      }
    } catch (e) {
      console.log('Erro ao carregar fonte do arquivo:', e)
    }
  },
  onDestroy() {
    console.log('MiniBíblia: App destruído')
  },
  globalData: {
    bookId: 1,
    bookName: 'Gênesis',
    chaptersCount: 50,
    chapter: 1,
    fontSize: 26
  }
})
