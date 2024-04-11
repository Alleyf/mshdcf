<div align='center'>
<img src="https://qnpicmap.fcsluck.top/pics/202312311949932.png" width="30%" height="30%" alt="coverage">
</div>
<div align='center'>

<img src="https://img.shields.io/github/stars/Alleyf/mshdcf.svg?style=social&label=Stars" alt="windows">
<img src="https://img.shields.io/badge/windows-10-blue.svg" alt="windows">
<img src="https://img.shields.io/badge/version-1.0.0-blue.svg" alt="version">
<img src="https://img.shields.io/badge/license-MIT-blue.svg" alt="license">
<img alt="Version" src="https://img.shields.io/badge/version-1.0.0-blue.svg?cacheSeconds=2592000" />
  <img src="https://img.shields.io/badge/node-%3E%3D16.13.0-blue.svg" />
  <a href="https://github.com/Alleyf/big-event#readme" target="_blank">
    <img alt="Documentation" src="https://img.shields.io/badge/documentation-yes-brightgreen.svg" />
  </a>
  <a href="https://github.com/Alleyf/big-event/graphs/commit-activity" target="_blank">
    <img alt="Maintenance" src="https://img.shields.io/badge/Maintained%3F-yes-green.svg" />
  </a>
  <a href="https://github.com/Alleyf/big-event/blob/master/LICENSE" target="_blank">
    <img alt="License: MIT" src="https://img.shields.io/github/license/Alleyf/big-event" />
  </a>
</div>

## Introduction

尽管目前智慧司法应用种类繁多，但多数仍然依赖非结构化文本数据存储技术。相较于结构化数据，并不利于计算机的分析理解和处理。此外，由于司法数据种类多样、来源众多并且数据关联融合难度大，尚未充分挖掘其深层潜在信息，导致面向司法下游任务的上层应用效果不够明显，仍需加强。因此，研究司法数据的汇聚融合与表征管理，有效挖掘数据的结构化特征，构建面向司法领域的互联网数据汇聚融合平台，合理利用多个数据源，打破数据孤岛，进行资源整合与优化配置，提高数据的利用率，从而实现多源、异构司法数据的汇聚融合、统一管理、深度治理和优化配置，有助于支撑司法数据的深度实践与应用，对于解决当前智慧司法实践中的难点和痛点，促进司法信息化和智能化建设，提升司法效能，支持司法管理决策，以及促进公平正义等方面具有重要的研究意义。

## KeyTechnique---PromptProject

#### 1. Judicial case

##### 1.1 Mainly modified template

> 修正下述司法案例正文的格式并纠正可能存在的语法错误后返回:

##### 1.2 Information mining template

返回结构：

```json
{
    "keyword": "",
    "summary": "",
    "plea": "",
    "label": "",
    "plai": "",
    "defe": "",
    "article": "xx",
    "party": {
        "plaintiff": "",
        "defendant": ""
    },
    "fact": "",
    "note": ""
}
```

字段说明：

* keyword(关键词，至多4个司法要素相关的词语，必要)
* summary(摘要总结至少100字，必要)
* plea(诉讼要求，必要)
* label(案件类型，必要)
* plai(原告诉述，必要)
* defe(被告辩称，必要)
* article(法律依据 第xx条，必要)
* party(当事人，即plaintiff（原告），defendant（被告），必要)
* fact(法院意见，如:认定如下../本院裁定如下...等，必要)
* note（审判员xxx，日期xxx，书记员xxx，必要）

参考示例：

```json
{
    "keyword": "行政处罚、冻结、划拨、银行存款、动产、不动产、民事诉讼法",
    "summary": "本案为申请执行人瓜州县消防救援大队与被执行人瓜州县三元物业服务有限公司之间的行政非诉执行案件。被执行人未履行生效法律文书确定的给付义务，法院根据相关法律规定裁定冻结、划拨被执行人银行存款，扣留、提取其应当履行部分的收入，或查封、扣押同等价值的财产。",
    "plea": "瓜州县消防救援大队要求瓜州县三元物业服务有限公司交纳12500元案款和88元执行费",
    "label": "行政非诉执行案",
    "plai": "瓜州县消防救援大队认为瓜州县三元物业服务有限公司未按规定交纳应缴费用",
    "defe": "瓜州县三元物业服务有限公司认为其无违规行为，不应交纳相关费用",
    "article": "《中华人民共和国民事诉讼法》第二百四十九条、第二百五十条、第二百五十一条；《最高人民法院关于适用《中华人民共和国民事诉讼法》的解释》第四百八十五条",
    "party": {
        "plaintiff": "瓜州县消防救援大队",
        "defendant": "瓜州县三元物业服务有限公司"
    },
    "fact": "因被执行人瓜州县三元物业服务有限公司拒不履行生效法律文书确定的给付义务，法院裁定冻结、划拨被执行人瓜州县三元物业服务有限公司银行存款人民币12588元，银行存款不足清偿的扣留、提取被执行人应当履行部分的收入或查封、扣押其同等价值的财产。",
    "note": "审判员陈典二〇二三年三月二十三日书记员丁宇婕"
}
```

目标文本：xxx······

结果要求：
将上述目标文本（司法案例文书正文内容）根据上述字段说明进行提取、修正，对为空字段值的按照理解生成补充对应内容，最后按照上述返回结构的json字符串返回给我

---

#### 2 Laws and regulations

##### 2.1 Mainly modified template

> 修正上述法条正文的格式并纠正可能存在的语法错误，按照章、条通过换行符\n分段后返回

##### 2.2 Information mining template

参考示例：

```json
{
    "field": "交通",
    "type": "行政法规",
    "organization": "国务院",
    "release": "2012-10-13",
    "execute": "2013-01-01",
    "basis": [
        "《中华人民共和国行政处罚法》",
        "《中华人民共和国防震减灾法》",
        "《中华人民共和国突发事件应对法》",
        "《中华人民共和国治安管理处罚法》"
    ],
    "scope": "在中华人民共和国管辖的通航水域内的经营性旅客运输和货物运输。",
    "main": [
        "规范国内水路运输经营行为，维护国内水路运输市场秩序，保障国内水路运输安全，促进国内水路运输业健康发展。",
        "明确水路运输经营者、水路运输辅助业务经营者的资格条件、经营范围、审批程序等。",
        "规定了水路运输经营者的权利和义务，以及水路运输辅助业务经营者的权利和义务。",
        "明确了水路运输经营者、水路运输辅助业务经营者的法律责任。"
    ],
    "abstract": "《国内水路运输管理条例》是国务院于2012年10月13日发布，并于2013年1月1日起施行的行政法规。该条例旨在规范国内水路运输经营行为，维护国内水路运输市场秩序，保障国内水路运输安全，促进国内水路运输业健康发展。条例对水路运输经营者、水路运输辅助业务经营者的资格条件、经营范围、审批程序、权利义务、法律责任等方面作出了详细规定。"
}

```

指令要求：
按照上述json字符串格式的参考示例对以下法条正文获取其所属领域[交通，教育，环境，金融，体育，文化，公共安全，医疗等领域之一]
、类型[法律，行政法规，地方性法规，司法解释，部门规章和其他 这6种之一]、颁布组织、发布及实施日期[yyyy-mm-dd]
、适用范围、法条依据[json数组]、主要内容[json数组]和摘要总结（不少于100字），确实无法提取得到的json字段内容则为空字符串，最终返回不带markdown代码格式的纯文本

法条正文：xxx······

---

## Usage

1. 安装maven依赖
2. 配置中间件(可以都用docker部署，docker目录下的docker-compose.yml)
    - `nacos`：服务注册
    - `elasticsearch`：全文检索
    - `redis`：服务缓存
    - `mysql`：数据持久
    - `minio`：对象存储
    - `rabbitmq`：消息队列
    - `sentinel`：流量控制
    - `seata`：分布式事务
    - `crawlab`：爬虫管理
    - `mongo`：爬虫数据存储
    - `metabase`：数据分析可视化
    - `ollama`：AI助理
3. 安装vue包依赖

```shell
npm install
```

4. 启动项目

```shell
npm run dev
```

## Tips

> 1. Vue 子路由地址不能一样，名称一样只有一个生效，导致另外的404报错

## Contributing

1. Fork it!
2. Create your feature branch: `git checkout -b my-new-feature`
3. Commit your changes: `git commit -am 'Add some feature'`
4. Push to the branch: `git push origin my-new-feature`
5. Submit a pull request

## Show your support

Give a ⭐️ if this project helped you!

## License

Copyright © 2024 [Alleyf](https://github.com/Alleyf).<br />
This project is [MIT](https://github.com/Alleyf/LawCrawler/main/LICENSE) licensed.

## Plans

- [ ] 
  支持更多司法相关数据开源的网站数据采集（eg：[国家法律法规数据库](https://flk.npc.gov.cn/)等）
- [ ] 降低数据处理对大语言模型的依赖程度/集成本地大语言模型进行数据处理和知识问答
- [ ] 采用[OpenAI](https://openai.com/)对司法数据进行分类、识别、纠错等数据清洗操作
- [ ] 采用[HanNLP](https://www.hanlp.com/)对采集到的法文进行分词、提取关键信息等
- [ ] 采用LangChain构建司法本地知识库实现RAG私有化知识问答

## Author

👤 **Alleyf**

* Website: https://alleyf.github.io/
* Github: [@Alleyf](https://github.com/Alleyf)

## Credits

- [@Alleyf](https://github.com/Alleyf)
- 其他贡献者

### 🏠 [Homepage](https://github.com/Alleyf/MSHDCF#readme)

## Star History

<picture>
    <source media="(prefers-color-scheme: dark)" srcset="https://api.star-history.com/svg?repos=Alleyf/LawCrawler&type=Date&theme=dark" />
    <source media="(prefers-color-scheme: light)" srcset="https://api.star-history.com/svg?repos=Alleyf/LawCrawler&type=Date" />
    <img alt="Star History Chart" src="https://api.star-history.com/svg?repos=Alleyf/LawCrawler&type=Date" />
</picture>