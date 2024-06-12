/*
 * Copyright (c) 2021, Alibaba Group Holding Limited;
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
export class Value {
  expression:string = ''
  source:object|string

  constructor(expression:string, source:object|string) {
    this.expression = expression
    this.source = source
  }
}

export function getTestExpressionList(): Array<Value> {
  let list:Array<Value> = []
  list.push(new Value("$$",
    "allData"
  ))
  list.push(new Value("$a.b",
    {
      "a":{
        "b":"bingo"
      }
    }
  ))
  list.push(new Value("$a.list",
    {
      "a":{
        "list":[
          'bingo',
          'error'
        ]
      }
    }
  ))
  list.push(new Value("$a.list[0]",
    {
      "a":{
        "list":[
          'bingo',
          'error'
        ]
      }
    }
  ))
  list.push(new Value("$a.list[0].b",
    {
      "a":{
        "list":[
          {
            "b":"bingo"
          },
          {
            "b1":"error"
          }
        ]
      }
    }
  ))
  list.push(new Value("$a.b == 'value' ? $a.b : 'noValue'",
    {
      "a":{
        "b":"bingo"
      }
    }
  ))
  list.push(new Value("size($a.b)",
    {
      "a":{
        "b":"bingo"
      }
    }
  ))
  return list
}