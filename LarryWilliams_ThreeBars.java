package strategies.examples;

import java.awt.Color;

import contangoAPI.api.ABaseStrategy;
import contangoAPI.api.Bars;
import contangoAPI.api.IArrayOfBars;
import contangoAPI.api.IPosition;
import contangoAPI.api.StrategyParameter;
import contangoAPI.api.Trade;
import contangoAPI.indicator.SMA;

public class LarryWilliams_ThreeBars extends ABaseStrategy {

  public void execute(IPosition pos, IArrayOfBars arBars) {

    if (arBars.size() < 1) // check that at least one symbol was
      return;
    final Bars bars = arBars.get(0); // get bars of the symbol
    final String sym = bars.getSym();
    // create indicators Highest & Lowest & MACD
    SMA sma = new SMA("", Color.BLUE, bars.closes, getParAsInt("sma"), 1);
    // starting position is max period of indicators
    int start = getParAsInt("sma");
    //
    // main cycle
    //
    for (int i = start; i < bars.closes.length; i++) {
      Trade trade = null;
      double v = sma.getData()[i];
      double c0 = bars.closes[i];
      boolean up0 = c0 > bars.opens[i];
      // if position is absent open position
      if (pos.getPos(sym) == IPosition.NOTHING) {
        double L2 = bars.lows[i - 2];
        double H2 = bars.highs[i - 2];
        double c2 = bars.closes[i - 2];
        double c1 = bars.closes[i - 1];
        boolean up2 = c2 > bars.opens[i - 2];
        boolean up1 = c1 > bars.opens[i - 1];
        // check green Bar
        if (up2 && up1 && up0) {
          // check 3 Bars more than SMA
          if (c2 > v && c1 > v && c0 > v) {
            // check last Bar High must be more than previous
            if (bars.highs[i - 2] < bars.highs[i] && bars.highs[i - 1] < bars.highs[i]) {
              // check Bar Low must be less than SMA
              if (L2 < v) {
                trade = pos.buyAtMarket(i + 1, bars);
              }
            }
          }
        }
        if (trade == null) {
          // check red Bar
          if (!up2 && !up1 && !up0) {
            // check 3 Bars less than sma
            if (c2 < v && c1 < v && c0 < v) {
              // check last Bar Low must be less than previous
              if (bars.lows[i - 2] > bars.lows[i] && bars.lows[i - 1] > bars.lows[i]) {
                // check Bar High must be more than SMA 
                if (H2 > v) {
                  trade = pos.shortAtMarket(i + 1, bars);
                }
              }
            }
          }
        }
        // if position is LONG try to close position
      } else if (pos.getPos(sym) == IPosition.LONG) {
        if (c0 < v)
          pos.sellAtMarket(i + 1, bars);
        // if position is SHORT try to close position
      } else if (pos.getPos(sym) == IPosition.SHORT) {
        if (c0 > v)
          pos.coverAtMarket(i + 1, bars);
      }
    }
    // draw indicator
    draw(sym, sma);
    // draw all trades
    draw(sym, pos.getDraw());
  }

  public void unload() {
  }

  public void load() {
    createStrategyParameter("sma", StrategyParameter.TYPE_INTEGER, 18, 5, 50, 1);
    initSettings("'3 Bars' by Larry Williams;\none symbol;\nLong and Short\nDaily");
  }

}
