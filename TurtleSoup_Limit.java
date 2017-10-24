package strategies.examples;

import java.awt.Color;

import contangoAPI.api.ABaseStrategy;
import contangoAPI.api.Bars;
import contangoAPI.api.IArrayOfBars;
import contangoAPI.api.IPosition;
import contangoAPI.api.StrategyParameter;
import contangoAPI.api.Trade;
import contangoAPI.indicator.Highest;
import contangoAPI.indicator.Lowest;

public class TurtleSoup_Limit extends ABaseStrategy {

  public void execute(IPosition pos, IArrayOfBars arBars) {

    if (arBars.size() < 1) // check that at least one symbol was
      return;

    final Bars bars = arBars.get(0); // get bars of the symbol
    final String sym = bars.getSym();
    // create indicators Highest & Lowest & EMA
    Highest high = new Highest("H", Color.BLUE, bars.highs, getParAsInt("highest"), 1);
    Lowest low = new Lowest("L", Color.RED, bars.lows, getParAsInt("lowest"), 1);
    //
    // starting position is max period of indicators
    //
    int start = Math.max(getParAsInt("highest"), getParAsInt("lowest"));
    //
    // main cycle
    //
    boolean bNextTradeCanBeLONG = true; // for alternating LONG SHORT if get loss in last trade
    double deltaTakeProfit = 0;
    double deltaStopLoss = 0;
    for (int i = start; i < bars.closes.length; i++) {

      // if position is absent open position
      if (pos.getPos(sym) == IPosition.NOTHING) {
        Trade t = null;
        if (bNextTradeCanBeLONG) {
          t = pos.buyAtLimit(i + 1, bars, low.getData()[i], "limit");
          if (t != null) {
            bNextTradeCanBeLONG = false;
            // calculate stop price
            deltaTakeProfit = high.getData()[i] - t.getEnter().getPrice();
            deltaStopLoss = deltaTakeProfit * 0.3; // 30 % takeProfit
          }
        } else if (t == null) {
          t = pos.shortAtLimit(i + 1, bars, high.getData()[i], "limit");
          if (t != null) {
            bNextTradeCanBeLONG = true;
            // calculate stop price
            deltaTakeProfit = t.getEnter().getPrice() - low.getData()[i];
            deltaStopLoss = deltaTakeProfit * 0.3; // 30 % takeProfit
          }
        }
      } else {
        // get last active trade
        Trade t = pos.getActiveTrades(sym).length > 0 ? pos.getActiveTrades(sym)[0] : null;
        // stop price is recommended to be first (i.e. any fixing of loss must
        // be before fixing of profit. It will be nearer to real results of
        // trading)
        if (pos.getPos(sym) == IPosition.LONG) {
          if (pos.sellAtStop(i + 1, bars, t.getEnter().getPrice() - deltaStopLoss, "stop loss") == null)
            pos.sellAtLimit(i + 1, bars, high.getData()[i], "take profit");
          else {
            bNextTradeCanBeLONG = false;
          }
        } else if (pos.getPos(sym) == IPosition.SHORT) {
          if (pos.coverAtStop(i + 1, bars, t.getEnter().getPrice() + deltaStopLoss, "stop loss") == null)
            pos.coverAtLimit(i + 1, bars, low.getData()[i], "take profit");
          else {
            bNextTradeCanBeLONG = true;
          }
        }
      }
    }
    // draw indicators
    draw(sym, high);
    draw(sym, low);
    // draw all trades
    draw(sym, pos.getDraw());
  }

  public void unload() {
  }

  public void load() {
    createStrategyParameter("highest", StrategyParameter.TYPE_INTEGER, 40, 1, 100, 1);
    createStrategyParameter("lowest", StrategyParameter.TYPE_INTEGER, 40, 1, 100, 1);
    initSettings("Turtle Soup strategy;\none symbol;\n60-min;");
  }

}
