package strategies.examples;

import java.awt.Color;

import contangoAPI.api.ABaseStrategy;
import contangoAPI.api.Bars;
import contangoAPI.api.IArrayOfBars;
import contangoAPI.api.IPosition;
import contangoAPI.api.StrategyParameter;
import contangoAPI.indicator.Highest;
import contangoAPI.indicator.Lowest;

public class Turtle_Stop extends ABaseStrategy {

  public void execute(IPosition pos, IArrayOfBars arBars) {

    // check that at least one symbol was
    if (arBars.size() < 1)
      return;
    
    final Bars bars = arBars.get(0); // get bars of the symbol
    final String sym = bars.getSym();
    //
    // create indicators Highest & Lowest & MACD
    //
    Highest high = new Highest("H", Color.BLUE, bars.highs, getParAsInt("highest"), 1);
    Lowest low = new Lowest("L", Color.RED, bars.lows, getParAsInt("lowest"), 1);
    //
    // starting position is max period of indicators
    //
    int start = Math.max(getParAsInt("highest"), getParAsInt("lowest"));
    //
    // main cycle
    //
    for (int i = start; i < bars.closes.length; i++) {
      // if position is absent open position
      if (pos.getPos(sym) == IPosition.NOTHING) {
        pos.buyAtStop(i + 1, bars, high.getData()[i], "buy stop");
        // if position is LONG try to close position
      } else if (pos.getPos(sym) == IPosition.LONG) {
        pos.sellAtStop(i + 1, bars, low.getData()[i], "sell stop");
      }
    }
    // draw Highest & Lowest
    draw(sym, high);
    draw(sym, low);
    // draw all trades
    draw(sym, pos.getDraw());
  }

  public void unload() {
  }

  public void load() {
    createStrategyParameter("highest", StrategyParameter.TYPE_INTEGER, 40, 10, 50, 1);
    createStrategyParameter("lowest", StrategyParameter.TYPE_INTEGER, 40, 10, 50, 1);
    initSettings("Turtle strategy;\none symbol;\nLong");
  }

}
