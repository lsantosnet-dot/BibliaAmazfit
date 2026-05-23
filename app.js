App({
  onCreate() {
    console.log('MiniBíblia: App criado')
  },
  onDestroy() {
    console.log('MiniBíblia: App destruído')
  },
  globalData: {
    bookId: 1,
    bookName: 'Gênesis',
    chaptersCount: 50,
    chapter: 1
  }
})
