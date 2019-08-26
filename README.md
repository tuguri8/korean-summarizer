## Korean-Summarizer

- Java library for summarizing Korean text by using TF-IDF algorithm
- OKT 형태소 분석기와 TF-IDF 알고리즘을 사용하여 한국어 텍스트의 키워드들 추출하고, 3줄 요약 기능을 제공하는 자바 라이브러리 입니다.

## Installation

#### Maven

```xml
<dependency>
  <groupId>com.github.tuguri8</groupId>
  <artifactId>korean-summarizer</artifactId>
  <version>0.1.2</version>
</dependency>
```

#### Gradle

```java
implementation 'com.github.tuguri8:korean-summarizer:0.1.2'
```

## Usage

#### **키워드 추출** (getKeywords)

텍스트를 문단으로 나누고, 2글자 이상의 명사만 태깅한 후 상위 5개의 TF-IDF 값을 가진 명사를 키워드로 반환합니다.

```java
KoreanSummarizer koreanSummarizer = new KoreanSummarizer();
koreanSummarizer.getKeywords("삼성전자가 올해 2분기 일본 스마트폰 시장에서 6년 만에 가장 높은 점유율을 기록한 것으로 나타났다. 상반기 출시한 프리미엄 스마트폰 '갤럭시S10'의 인기를 누린 영향으로 분석된다....")
    
// [갤럭시, 올림픽, 기업, 상승, 차지]
```

#### **3줄 요약(summarize)**

5개의 키워드를 추출 후, 해당 키워드에 대한 문장 내 명사들의 TF-IDF 값을 구해 모두 더하여 상위 3개의 문장을 이어서 반환합니다.

```java
KoreanSummarizer koreanSummarizer = new KoreanSummarizer();
koreanSummarizer.summarize("18∼24세 연령층에서 넷플릭스 등 유료 동영상 콘텐츠 플랫폼을 가장 많이 이용하는 것으로 나타났다.심동녁 정보통신정책연구원 부연구위원은 26일 '디지털 콘텐츠 이용현황 : 유료 서비스 이용자를 중심으로' 보고서에서 2018년 디지털 콘텐츠 유료서비스 이용비율이 가장 높은 연령층은 18∼24세(34.5%)였다고 밝혔다....")

// 이 보고서는 매년 실시되는 표본 추적조사인 한국미디어패널조사 결과를 토대로 작성됐다. 18∼24세 연령층에서 넷플릭스 등 유료 동영상 콘텐츠 플랫폼을 가장 많이 이용하는 것으로 나타났다. 콘텐츠 유형별로 보면 음악 서비스 이용비율이 18.4%로 가장 높게 나타났으며, 게임(15.4%), 신문·잡지·책(15.3%), 동영상·영화(15.1%), TV방송(8.4%), 교육동영상(5.5%) 등이었다.
```

## License

[Apache License 2.0](./LICENSE)

## Resources

[open-korean-text](<https://github.com/open-korean-text/open-korean-text>)

