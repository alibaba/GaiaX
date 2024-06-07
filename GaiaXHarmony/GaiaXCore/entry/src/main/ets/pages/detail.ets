import { MainPage } from 'gaiaxcoreharmony/Index'

@Entry({ routeName: "FilmDetailPage" })
@Component
export struct FilmDetailPage {

  @State dataList:Array<number> = [1]
  aboutToAppear() {
    for (let i = 20 - 1; i > 0; i--) {
      this.dataList.push(i)
    }
  }

  aboutToDisappear() {

  }


  build() {
    List(){
      ListItem() {
        Column() {
          Text("header0").width('100%').height(500)
        }.backgroundColor('#ff0000')
      }
      ListItem() {
        Column() {
          Text("header1").width('100%').height(500)
        }.backgroundColor('#00ff00')
      }
      ListItem() {
        Column() {
          Text("header2").width('100%').height(500)
        }.backgroundColor('#0000ff')
      }
      ListItem() {
        Column() {
          Text("header3").width('100%').height(500)
        }.backgroundColor('#999999')
      }
      ListItem(){
        List(){
          ForEach(this.dataList,(item:number) => {
            ListItem(){
              builderItem()
            }
          },(item:number) => {
            return `${item}`
          })
        }
      }

    }.height('100%')
    .backgroundColor("#f4f5f6")
  }
}

@Builder
function builderItem() {
  MainPage()
}