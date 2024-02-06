package com.ruoyi.common.core.utils;

import com.kennycason.kumo.CollisionMode;
import com.kennycason.kumo.WordCloud;
import com.kennycason.kumo.WordFrequency;
import com.kennycason.kumo.bg.CircleBackground;
import com.kennycason.kumo.font.KumoFont;
import com.kennycason.kumo.font.scale.SqrtFontScalar;
import com.kennycason.kumo.nlp.FrequencyAnalyzer;
import com.kennycason.kumo.nlp.tokenizers.ChineseWordTokenizer;
import com.kennycason.kumo.palette.LinearGradientColorPalette;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Base64;
import java.util.List;

/**
 * 词云工具包
 *
 * @author fcs
 * @date 2024/2/4 19:39
 * @site <a href="https://alleyf.github.io">getHelp</a>
 * @description
 */
public class WorldCloudUtils {

    public static final FrequencyAnalyzer FREQUENCY_ANALYZER = new FrequencyAnalyzer();
    public static final String BASE64_PREFIX = "data:image/png;base64,";

    static {
        //建立词频分析器，设置词频，以及词语最短长度，此处的参数配置视情况而定即可
        FREQUENCY_ANALYZER.setWordFrequenciesToReturn(40);
        FREQUENCY_ANALYZER.setMinWordLength(2);
        //引入中文解析器
        FREQUENCY_ANALYZER.setWordTokenizer(new ChineseWordTokenizer());
    }

    public static String genWorldCloud(String name, String text) {
        try {
            //指定源文本，生成词频集合
//            text = StringUtils.stripCaseUnicode(text);
            List<String> textLs = StringUtils.str2List(text, "\n", true, true);
//            System.out.println("词频集合大小：" + textLs.size());
            List<WordFrequency> wordFrequencyList = FREQUENCY_ANALYZER.load(textLs);
            //设置图片分辨率
            WordCloud wordCloud = getWordCloud();
            //生成词云
            wordCloud.build(wordFrequencyList);
//        保存词云
//             获取当前线程的ClassLoader
//            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
////             获取src/main/resources目录的URI
//            URI resourceUri = Objects.requireNonNull(classLoader.getResource("")).toURI();
//            wordCloud.writeToFile(resourceUri.getPath() + "wordCloud/" + name + ".png");
//             将词云图像转换为BufferedImage
            BufferedImage bufferedImage = wordCloud.getBufferedImage();
            // 将BufferedImage转换为ByteArrayOutputStream
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", outputStream);
            // 关闭流
            outputStream.flush();
            outputStream.close();
            // 将字节数组转换为Base64编码的字符串
            byte[] imageBytes = outputStream.toByteArray();
            return StringUtils.join(BASE64_PREFIX, Base64.getEncoder().encodeToString(imageBytes));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private static WordCloud getWordCloud() throws IOException {
        Dimension dimension = new Dimension(640, 640);
        //此处的设置采用内置常量即可，生成词云对象
        WordCloud wordCloud = new WordCloud(dimension, CollisionMode.PIXEL_PERFECT);
        //设置边界及字体
        wordCloud.setPadding(2);
        Font font = new Font("Meiryo", Font.PLAIN, 50);
        //设置词云显示的三种颜色，越靠前设置表示词频越高的词语的颜色
        wordCloud.setColorPalette(new LinearGradientColorPalette(Color.RED, Color.BLUE, Color.GREEN, 30, 30));
        wordCloud.setKumoFont(new KumoFont(font));
        //设置背景色
        wordCloud.setBackgroundColor(new Color(255, 255, 255));
        //设置背景图片
//        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
////        ClassPathResource resource = new ClassPathResource("wordCloud/bg.png");
//        InputStream inputStream = classLoader.getResourceAsStream("wordCloud/bg.png");
//        if (inputStream != null) {
//            wordCloud.setBackground(new PixelBoundaryBackground(inputStream));
//        } else {
//            throw new IOException("背景图片未找到");
//        }
        //设置背景图层为圆形
        wordCloud.setBackground(new CircleBackground(320));
        wordCloud.setFontScalar(new SqrtFontScalar(10, 100));
        return wordCloud;
    }

    public static void main(String[] args) throws IOException, URISyntaxException {
        String str = "(2023)甘0922执509号申请执行人：瓜州县消防救援大队。法定代表人：任某某。被执行人：瓜州县三元物业服务有限公司。法定代表人：王某某。申请执行人瓜州县消防救援大队与被执行人瓜州县三元物业服务有限公司行政非诉执行一案，依据（2023）甘0922行审1号行政处罚，瓜州县三元物业服务有限公司应交纳案件款12500元，执行费88元，合计12588元。\uE292因被执行人瓜州县三元物业服务有限公司拒不履行生效法律文书确定的给付义务，依照《中华人民共和国民事诉讼法》第二百四十九条、第二百五十条、第二百五十一条、《最高人民法院关于适用《中华人民共和国民事诉讼法》的解释》第四百八十五条规定，裁定如下：冻结、划拨被执行人瓜州县三元物业服务有限公司银行存款人民币12588元，银行存款不足清偿的，扣留、提取被执行人应当履行部分的收入或查封、扣押其同等价值的房产、土地使用权、汽车、股权、设备等财产。冻结银行存款的期限为一年，查封、扣押动产的期限为两年，查封不动产、冻结其他财产权的期限为三年。本裁定立即执行。审判员陈典二〇二三年三月二十三日书记员丁宇婕";
        String base64Img = WorldCloudUtils.genWorldCloud("test", str);
//        System.out.println(base64Img);
    }
}
