package strategies.examples;

import java.awt.Color;

import contangoAPI.api.ABaseStrategy;
import contangoAPI.api.Bars;
import contangoAPI.api.IArrayOfBars;
import contangoAPI.api.IPosition;
import contangoAPI.api.StrategyParameter;
import contangoAPI.graph.Line;
import contangoAPI.indicator.ATR;
import contangoAPI.indicator.Highest;
import contangoAPI.indicator.Lowest;
import contangoAPI.indicator.MACD;

public class DrawSomeIndicators extends ABaseStrategy {

  public void execute(IPosition pos, IArrayOfBars arBars) {

    if (arBars.size() < 1) // check that at least one symbol was
      return;
    // get bars of the symbol
    final Bars bars = arBars.get(0);
    // create MACD indicator
    MACD macd = new MACD("MACD", Color.ORANGE, bars.closes, getParAsInt("macd1"), getParAsInt("macd2"), 2);
    // create ATR indicator
    ATR atr = new ATR("ATR", Color.BLUE, bars, getParAsInt("atr"), 3);
    // create Highest indicator
    Highest high = new Highest("H", Color.BLUE, bars.highs, getParAsInt("highest"), 1);
    // create Lowest indicator
    Lowest low = new Lowest("L", Color.RED, bars.lows, getParAsInt("lowest"), 1);

    // draw High/Low on Price panel
    draw(bars.getSym(), high);
    draw(bars.getSym(), low);
    // draw MACD on panel named "123"
    draw("123", macd);
    // draw horizon Line on panel named "123"
    draw("123", Line.create("zero line", 0, 0, bars.closes.length - 1, 0, Color.RED, 1, Line.SOLID));
    // draw ATR on panel named "12"
    draw("12", atr);
    // draw horizon Line on panel named "12"
    draw("12", Line.create("zero line", 0, 1, bars.closes.length - 1, 1, Color.RED, 1, Line.DASHED));
  }

  public void unload() {
  }

  public void load() {
    createStrategyParameter("highest", StrategyParameter.TYPE_INTEGER, 20, 1, 50, 1);
    createStrategyParameter("lowest", StrategyParameter.TYPE_INTEGER, 20, 1, 50, 1);
    createStrategyParameter("atr", StrategyParameter.TYPE_INTEGER, 14, 1, 50, 1);
    createStrategyParameter("macd1", StrategyParameter.TYPE_INTEGER, 5, 1, 50, 1);
    createStrategyParameter("macd2", StrategyParameter.TYPE_INTEGER, 34, 1, 50, 1);
    initSettings("Just drawing some Indicators");
  }

}
