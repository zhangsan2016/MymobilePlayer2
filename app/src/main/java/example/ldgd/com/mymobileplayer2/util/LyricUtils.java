package example.ldgd.com.mymobileplayer2.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import example.ldgd.com.mymobileplayer2.domain.Lyric;

/**
 * Created by ldgd on 2018/7/24.
 * 功能： 歌词解析工具类
 * 说明： readLyricFile-parsedLyric-strTime2LongTime
 */

public class LyricUtils {
    /**
     * 歌词是否存在
     */
    private boolean isExistsLyric;
    /**
     * 解析好的歌词列表
     */
    private List<Lyric> lyrics;

    public boolean isExistsLyric() {
        return isExistsLyric;
    }

    public List<Lyric> getLyrics() {
        return lyrics;
    }

    public void readLyricFile(File file) {

        if (file == null && !file.exists()) {
            // 歌词不存在
            isExistsLyric = false;
            lyrics = null;
        } else {

            //歌词文件存在
            //1.解析歌词 一行的读取-解析
            lyrics = new ArrayList<>();
            isExistsLyric = true;

            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(file),getCharset(file)));

                String line = "";
                while ((line = reader.readLine()) != null) {
                    // 解析一句歌词
                    parsedLyric(line);
                }
                reader.close();


            } catch (Exception e) {
                e.printStackTrace();
                isExistsLyric = false;
            }

            //2.排序
            Collections.sort(lyrics, new Comparator<Lyric>() {
                @Override
                public int compare(Lyric lhs, Lyric rhs) {
                    if (lhs.getTimePoint() > rhs.getTimePoint()) {
                        return 1;
                    } else if (lhs.getTimePoint() > rhs.getTimePoint()) {
                        return -1;
                    } else {
                        return 0;
                    }
                }
            });

            //3.计算每句高亮显示的时间
            for (int i = 0; i < lyrics.size(); i++) {
                Lyric oneLyric = lyrics.get(i);
                if(i+1 <lyrics.size()){
                    Lyric  twoLyric = lyrics.get(i+1);
                    oneLyric.setSleepTime(twoLyric.getTimePoint() - oneLyric.getTimePoint());
                }
            }


        }
    }

    /**
     * 解析一句歌词
     *
     * @param line [02:04.12][03:37.32][00:59.73]我在这里欢笑
     */
    private String parsedLyric(String line) {

        // indexOf 第一次出现的位置
        int pos1 = line.indexOf("[");  // 0,如果没有返回-1
        int pos2 = line.indexOf("]");  // 9,如果没有返回-1

        if (pos1 == 0 && pos2 != -1) {

            // 保存时间数组，getCountTag() 判断有多少句歌词
            long[] times = new long[getCountTag(line)];

            // 截取歌词，并把歌词对应的时间转换为long类型保存到数组
            String strTime = line.substring(pos1 + 1, pos2);
            times[0] = strTime2LongTime(strTime);

            String content = null;
            int i = 1;
            while (pos1 == 0 && pos2 != -1) {
                content = line.substring(pos2 + 1);

                pos1 = content.indexOf("[");  // 0,如果没有返回-1
                pos2 = content.indexOf("]");  // 9,如果没有返回-1

                if (pos1 != -1) {
                    strTime = content.substring(pos1 + 1, pos2);
                    times[i] = strTime2LongTime(strTime);

                    if (times[i] == -1) {
                        return "";
                    }
                    i++;
                }


                // 把这句歌词保存到集合中，关联时间数组和文本
                for (int j = 0; j < times.length; j++) {
                    Lyric lyric = new Lyric();

                    if (times[j] != 0) { // 有时间戳

                        lyric.setContent(content);
                        lyric.setTimePoint(times[j]);

                        // 添加到集合中
                        lyrics.add(lyric);

                    }
                }
            }
            return content;
        }

        return "";
    }

    /**
     * 把String类型是时间转换成long类型
     *
     * @param strTime 02:04.12
     * @return
     */
    private long strTime2LongTime(String strTime) {

        long result = -1;

        try {

            // 1.把02:04.12按照“：”切割成02和04.12
            String[] s1 = strTime.split(":");
            // 2.把04.12按照“.”切割成04和12
            String[] s2 = s1[1].split("\\.");

            // 分
            long min = Long.parseLong(s1[0]);

            // 秒
            long second = Long.parseLong(s2[0]);

            // 毫秒
            long mil = Long.parseLong(s2[1]);

            result = min * 60 * 1000 + second * 1000 + mil * 10;

        } catch (Exception e) {
            e.printStackTrace();
            result = -1;
        }
        return result;
    }

    /**
     * 判断有多少句歌词
     *
     * @param line [02:04.12][03:37.32][00:59.73]我在这里欢笑
     * @return
     */
    private int getCountTag(String line) {
        int result = -1;
        // 截取的时候添加转义符 "\\["
        String[] left = line.split("\\[");
        String[] right = line.split("\\]");

        // 取长度大的作为数组长度
        if (left.length > right.length) {
            result = left.length;
        } else {
            result = right.length;
        }

        return result;
    }


    /**
     * 判断文件编码
     * @param file 文件
     * @return 编码：GBK,UTF-8,UTF-16LE
     */
    public String getCharset(File file) {
        String charset = "GBK";
        byte[] first3Bytes = new byte[3];
        try {
            boolean checked = false;
            BufferedInputStream bis = new BufferedInputStream(
                    new FileInputStream(file));
            bis.mark(0);
            int read = bis.read(first3Bytes, 0, 3);
            if (read == -1)
                return charset;
            if (first3Bytes[0] == (byte) 0xFF && first3Bytes[1] == (byte) 0xFE) {
                charset = "UTF-16LE";
                checked = true;
            } else if (first3Bytes[0] == (byte) 0xFE
                    && first3Bytes[1] == (byte) 0xFF) {
                charset = "UTF-16BE";
                checked = true;
            } else if (first3Bytes[0] == (byte) 0xEF
                    && first3Bytes[1] == (byte) 0xBB
                    && first3Bytes[2] == (byte) 0xBF) {
                charset = "UTF-8";
                checked = true;
            }
            bis.reset();
            if (!checked) {
                int loc = 0;
                while ((read = bis.read()) != -1) {
                    loc++;
                    if (read >= 0xF0)
                        break;
                    if (0x80 <= read && read <= 0xBF)
                        break;
                    if (0xC0 <= read && read <= 0xDF) {
                        read = bis.read();
                        if (0x80 <= read && read <= 0xBF)
                            continue;
                        else
                            break;
                    } else if (0xE0 <= read && read <= 0xEF) {
                        read = bis.read();
                        if (0x80 <= read && read <= 0xBF) {
                            read = bis.read();
                            if (0x80 <= read && read <= 0xBF) {
                                charset = "UTF-8";
                                break;
                            } else
                                break;
                        } else
                            break;
                    }
                }
            }
            bis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return charset;
    }


}


