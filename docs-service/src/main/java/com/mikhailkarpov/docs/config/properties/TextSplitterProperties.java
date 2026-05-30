package com.mikhailkarpov.docs.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.ai.text-splitter")
public class TextSplitterProperties {

  private int chunkSize = 800;
  private int minChunkSizeChars = 350;
  private int minChunkLengthToEmbed = 5;
  private int maxNumChunks = 10000;
  private boolean keepSeparator = true;

  public int getChunkSize() {
    return chunkSize;
  }

  public void setChunkSize(int chunkSize) {
    this.chunkSize = chunkSize;
  }

  public int getMinChunkSizeChars() {
    return minChunkSizeChars;
  }

  public void setMinChunkSizeChars(int minChunkSizeChars) {
    this.minChunkSizeChars = minChunkSizeChars;
  }

  public int getMinChunkLengthToEmbed() {
    return minChunkLengthToEmbed;
  }

  public void setMinChunkLengthToEmbed(int minChunkLengthToEmbed) {
    this.minChunkLengthToEmbed = minChunkLengthToEmbed;
  }

  public int getMaxNumChunks() {
    return maxNumChunks;
  }

  public void setMaxNumChunks(int maxNumChunks) {
    this.maxNumChunks = maxNumChunks;
  }

  public boolean isKeepSeparator() {
    return keepSeparator;
  }

  public void setKeepSeparator(boolean keepSeparator) {
    this.keepSeparator = keepSeparator;
  }
}
