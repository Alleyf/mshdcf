**Read this in other languages: [English](README.md), [中文](README_zh.md).**

# MSHDCF

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

> This Project Uses Scrapy combined with Selenium to collect some judicial-related data for learning and research.
> Currently, only the following website is being scraped:<br>

> [OpenLaw](https://openlaw.cn/)

## Structure

```
.
├---openlaw
    |   decrypt.py
    |   items.py
    |   list-data-process.js
    |   middlewares.py
    |   openlaw_util.py
    |   pipelines.py
    |   settings.py
    |   start.py
    |   __init__.py
    |
    +---spiders
    |   |   openlaw_case_spider.py
    |   |   openlaw_law_spider.py
    |   |   __init__.py
├── README.md
├── requirements.txt
└── scrapy.cfg
└── .gitignore
└── LICENSE
└── openlaw_case.json
└── openlaw_law.json
```

## Install

Install project dependencies

```python
pip install -r requirements.txt 
```

- <img src="https://img.shields.io/badge/python-3.9.8-blue.svg" alt="python">
- <img src="https://img.shields.io/badge/scrapy-2.11.0-blue.svg" alt="scrapy">
- <img src="https://img.shields.io/badge/selenium-4.16.0-blue.svg" alt="selenium">
- <img src="https://img.shields.io/badge/mysql-8.0.27-blue.svg" alt="mysql">
- <img src="https://img.shields.io/badge/mysqlclient-2.0.3-blue.svg" alt="mysqlclient">
- <img src="https://img.shields.io/badge/chrome-91.0.4472.114-blue.svg" alt="chrome">
- <img src="https://img.shields.io/badge/chromedriver-91.0.4472.106-blue.svg" alt="chromedriver">
- <img src="https://img.shields.io/badge/geckodriver-120.0.9-blue.svg" alt="geckodriver">
- <img src="https://img.shields.io/badge/firefox-120.0.10-blue.svg" alt="firefox">
- ···

## Usage

Modify the `ITEM_PIPELINES` in `settings.py` according to the two crawlers

```python
ITEM_PIPELINES = {
   "openlaw.pipelines.OpenlawCasePipeline": 300,
  #  "openlaw.pipelines.OpenlawLawPipeline": 300,
   "openlaw.pipelines.SaveCaseFilePipeline": 320,
  #  "openlaw.pipelines.SaveLawFilePipeline": 320,
   "openlaw.pipelines.MysqlCasePipeline": 420,
  #  "openlaw.pipelines.MysqlLawPipeline": 420,
}
```

Also, modify the file storage location according to the crawler in `settings.py`, set `FILES_STORE`

```python
FILES_STORE = "./data/files"
```

Run the crawler

```sh
scrapy crawl open_case -o openlaw_case.json
scrapy crawl open_law -o openlaw_law.json
```

eg: `openlaw_case.json`

```json
{
    "case_url_code": "7346d81943904086948430cd3aae94ff",
    "case_name": "张某、刘某等赌博二审刑事裁定书",
    "case_date": "2023-03-27",
    "case_court": "辽宁省沈阳市中级人民法院",
    "case_number": "（2023）辽01刑终142号",
    "case_content": "原公诉机关沈阳市辽中区人民检察院。\n上诉人张某某1\n因涉嫌犯赌博罪于2021年3月3日被取保候审，2022年3月2日被监视居住，2023年1月31日被逮捕。\n现羁押于沈阳市第一看守所。\n原审被告人刘某某1\n曾于2010年3月20日因犯非法拘禁罪被判处有期徒刑六个月。\n因涉嫌犯赌博罪于2021年3月3日被取保候审，2022年3月2日被监视居住。\n原审被告人田某某1\n因涉嫌犯赌博罪于2021年3月3日被取保候审，2022年3月2日被监视居住。\n沈阳市辽中区人民法院审理沈阳市辽中区人民检察院指控原审被告人张某某1、刘某某1、田某某1犯赌博罪一案，于2023年1月31日作出（2022）辽0115刑初214号刑事判决，认定被告人张某某1犯赌博罪，判处有期徒刑七个月，并处罚金人民币二万元；被告人刘某某1犯赌博罪，判处有期徒刑六个月，缓刑一年，并处罚金人民币二万元；被告人田某某1犯赌博罪，判处有期徒刑六个月，缓刑一年，并处罚金人民币二万元。\n原审被告人张某某1不服原判，以原判对其量刑过重为由提出上诉。\n本院审理过程中，上诉人张某某1申请撤回上诉。\n本院认为，原审判决认定事实及适用法律正确，量刑适当，审判程序合法。\n上诉人张某某1撤回上诉的申请符合法律规定，应予准许。\n依照《最高人民法院关于适用《中华人民共和国刑事诉讼法》的解释》第三百八十三条  第二款  之规定，裁定如下：\n准许上诉人张某某1撤回上诉。\n沈阳市辽中区人民法院（2022）辽0115刑初214号刑事判决自本裁定送达之日起发生法律效力。\n本裁定为终审裁定。\n审判长王宁\n审判员韩宇川\n审判员李楠\n二〇二三年三月二十七日\n法官助理程英楠\n书记员姚雨彤",
    "case_cause": "赌博罪",
    "case_type_id": 2,
    "case_process": "二审",
    "source": "openlaw",
    "case_label": "取保候审#监视居住",
    "related_cases": "[{\"caseCode\": \"f89748ef29f8452d961839a23baafc19\", \"caseName\": \"\\u4f55\\u6587\\u82f1\\u4e0e\\u674e\\u79c0\\u6770\\u3001\\u4e01\\u7acb\\u519b\\u7b2c\\u4e09\\u4eba\\u64a4\\u9500\\u4e4b\\u8bc9\\u4e8c\\u5ba1\\u88c1\\u5b9a\\u4e66\"}, {\"caseCode\": \"0ec5f6eb9d7e47c79d50cc529353fde3\", \"caseName\": \"\\u738b\\u723d\\u3001\\u8d75\\u598d\\u8d4c\\u535a\\u4e8c\\u5ba1\\u5211\\u4e8b\\u88c1\\u5b9a\\u4e66\"}, {\"caseCode\": \"93a53b8c49dc469d9ce42b097e5c903f\", \"caseName\": \"\\u5f90\\u67d0\\u67d0\\u8d4c\\u535a\\u5211\\u4e8b\\u4e8c\\u5ba1\\u5211\\u4e8b\\u88c1\\u5b9a\\u4e66\"}, {\"caseCode\": \"81f05113ebb242b68a4d505fe7350788\", \"caseName\": \"\\u5468\\u5c0f\\u83b9\\u3001\\u5f20\\u6625\\u9633\\u7b49\\u63a9\\u9970\\u3001\\u9690\\u7792\\u72af\\u7f6a\\u6240\\u5f97\\u3001\\u72af\\u7f6a\\u6240\\u5f97\\u6536\\u76ca\\u5211\\u4e8b\\u4e8c\\u5ba1\\u5211\\u4e8b\\u88c1\\u5b9a\\u4e66\"}]",
    "file_urls": [
        "http://openlaw.cn/pdf/judgement/7346d81943904086948430cd3aae94ff"
    ]
}
```

eg: `openlaw_law.json`

```json
{
    "law_url_code": "7fc80cce64794b7c8ece212c8eef6922",
    "law_name": "全国人民代表大会常务委员会关于修改《中华人民共和国工会法》的决定",
    "source_id": 1,
    "release_organization": "全国人民代表大会常务委员会",
    "law_type_id": 1,
    "is_validity": 1,
    "release_date": "2021-12-24",
    "execute_date": "2022-01-01",
    "law_structure": null,
    "revise_num": null,
    "law_content": "\n\t\t\t\t\t\t\t\n全国人民代表大会常务委员会关于修改\n《中华人民共和国工会法》的决定\n（2021年12月24日第十三届全国人民代表大会常务\n委员会第三十二次会议通过）\n第十三届全国人民代表大会常务委员会第三十二次会议决定对《中华人民共和国工会法》作如下修改:\n一、将第二条第一款修改为:“工会是中国共产党领导的职工自愿结合的工人阶级群众组织，是中国共产党联系职工群众的桥梁和纽带。”···",
    "file_urls": [
        "http://openlaw.cn/pdf/law/7fc80cce64794b7c8ece212c8eef6922"
    ],
    "files": [
        {
            "url": "http://openlaw.cn/pdf/law/7fc80cce64794b7c8ece212c8eef6922",
            "path": "全国人民代表大会常务委员会关于修改《中华人民共和国工会法》的决定.pdf",
            "checksum": "e56206382d050ba4b2250fec63bebd1a",
            "status": "uptodate"
        }
    ]
}
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
  支持更多司法相关网站数据采集（eg：[北大法宝](https://www.pkulaw.com/)、[国家法律法规数据库](https://flk.npc.gov.cn/)、[把手案例](https://www.lawsdata.com/#/home)
  等）
- [ ] 集成该爬虫到SpringBoot项目中构建多源异构数据汇聚融合处理平台
- [ ] 采用[HanNLP](https://www.hanlp.com/)对采集到的法文进行分词、提取关键信息等
- [ ] 采用深度学习技术对法文进行分类、识别、纠错等数据清洗操作

## Author

👤 **Alleyf**

* Website: https://alleyf.github.io/
* Github: [@Alleyf](https://github.com/Alleyf)

## Credits

- [@Alleyf](https://github.com/Alleyf)
- 其他贡献者

### 🏠 [Homepage](https://github.com/Alleyf/LawCrawler#readme)

## Star History

<picture>
    <source media="(prefers-color-scheme: dark)" srcset="https://api.star-history.com/svg?repos=Alleyf/LawCrawler&type=Date&theme=dark" />
    <source media="(prefers-color-scheme: light)" srcset="https://api.star-history.com/svg?repos=Alleyf/LawCrawler&type=Date" />
    <img alt="Star History Chart" src="https://api.star-history.com/svg?repos=Alleyf/LawCrawler&type=Date" />
</picture>