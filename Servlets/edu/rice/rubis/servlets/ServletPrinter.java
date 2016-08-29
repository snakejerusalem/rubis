package edu.rice.rubis.servlets;

import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.GregorianCalendar;

import javax.servlet.http.HttpServletResponse;

/** In fact, this class is not a servlet itself but it provides
 * output services to servlets to send back HTML files.
 * @author <a href="mailto:cecchet@rice.edu">Emmanuel Cecchet</a> and <a href="mailto:julie.marguerite@inrialpes.fr">Julie Marguerite</a>
 * @version 1.0
 */

public class ServletPrinter
{
  private PrintWriter out;
  private String servletName;
  private GregorianCalendar startDate;

  public ServletPrinter(
    HttpServletResponse toWebServer,
    String callingServletName)
  {
    startDate = new GregorianCalendar();
    toWebServer.setContentType("text/html");
    try
    {
      out = toWebServer.getWriter();
    }
    catch (IOException e)
    {
      RubisHttpServlet.printError("Problem while ccreating ServletPrinter.", this);
      RubisHttpServlet.printException(e, this);
    }
    servletName = callingServletName;
  }

  public PrintWriter getOut() {
      return out;
  }

  public String getServletName() {
      return servletName;
  }

  public GregorianCalendar getStartDate() {
      return startDate;
  }
    
  void printFile(String filename)
  {
    FileReader fis = null;
    try
    {
      fis = new FileReader(filename);
      char[] data = new char[4 * 1024]; // 4K buffer
      int bytesRead;
      bytesRead = fis.read(data);
      while (/*(bytesRead = fis.read(data))*/
        bytesRead != -1)
      {
        out.write(data, 0, bytesRead);
        bytesRead = fis.read(data);
      }
    }
    catch (Exception e)
    {
      RubisHttpServlet.printError("Unable to read file.", this);
      RubisHttpServlet.printException(e, this);
    }
    finally
    {
      if (fis != null)
        try
        {
          fis.close();
        }
        catch (Exception e)
        {
            RubisHttpServlet.printError("Unable to close file reader.", this);
            RubisHttpServlet.printException(e, this);
        }
    }
  }

  void printHTMLheader(String title)
  {
     out.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\"\n" +
            
    "\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n" +
    "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n\n" +
            
    "<head>\n" +
        "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=iso-8859-1\">\n" +
        "<meta name=\"GENERATOR\" content=\"Mozilla/4.72 [en] (X11; U; Linux 2.2.14-5.0 i686) [Netscape]\">\n" +
        "<meta name=\"Author\" content=\"Julie Marguerite\">\n" +
           
        "<title>" + title + "</title>\n" +
    "</head>\n\n");
    
    printFile(Config.HTMLFilesPath + "/header.html");
 
  }

  void printHTMLfooter()
  {
    GregorianCalendar endDate = new GregorianCalendar();

    out.println(
      "<br /><hr />RUBiS (C) Rice University/INRIA<br /><i>Page generated by "
        + servletName
        + " in "
        + TimeManagement.diffTime(startDate, endDate)
        + "</i><br />");
    out.println("</body>");
    out.println("</html>");
  }

  void printHTML(String msg)
  {
    out.println(msg);
  }

  void printHTMLHighlighted(String msg)
  {
    out.println("<table width=\"100%\" bgcolor=\"#CCCCFF\">");
    out.println(
      "<tr><td align=\"center\" width=\"100%\"><font size=\"4\" color=\"#000000\"><b>"
        + msg
        + "</b></font></td></tr>");
    out.println("</table><br />");
  }

  ////////////////////////////////////////
  // Category related printed functions //
  ////////////////////////////////////////

  void printCategory(String categoryName, int categoryId)
  {
    try
    {
      out.println(
        "<a href=\"edu.rice.rubis.servlets.SearchItemsByCategory?category="
          + categoryId
          + "&categoryName="
          + URLEncoder.encode(categoryName)
          + "\">"
          + categoryName
          + "</a><br />");
    }
    catch (Exception e)
    {
      RubisHttpServlet.printError("Unable to print category.", this);
      RubisHttpServlet.printException(e, this);
    }
  }

  /** List all the categories with links to browse items by region */
  void printCategoryByRegion(String categoryName, int categoryId, int regionId)
  {
    try
    {
      out.println(
        "<a href=\"edu.rice.rubis.servlets.SearchItemsByRegion?category="
          + categoryId
          + "&categoryName="
          + URLEncoder.encode(categoryName)
          + "&region="
          + regionId
          + "\">"
          + categoryName
          + "</a><br />");
    }
    catch (Exception e)
    {
        
      RubisHttpServlet.printError("Unable to print category.", this);
      RubisHttpServlet.printException(e, this);
    }
  }

  /** Lists all the categories and links to the sell item page*/
  void printCategoryToSellItem(String categoryName, int categoryId, int userId)
  {
    try
    {
      out.println(
        "<a href=\"edu.rice.rubis.servlets.SellItemForm?category="
          + categoryId
          + "&user="
          + userId
          + "\">"
          + categoryName
          + "</a><br />");
    }
    catch (Exception e)
    {
      RubisHttpServlet.printError("Unable to print category.", this);
      RubisHttpServlet.printException(e, this);
    }
  }

  //////////////////////////////////////
  // Region related printed functions //
  //////////////////////////////////////

  void printRegion(int regionId, String regionName)
  {
    try
    {
      out.println(
        "<a href=\"edu.rice.rubis.servlets.BrowseCategories?region="
          + regionId
          + "\">"
          + regionName
          + "</a><br />");
    }
    catch (Exception e)
    {
      RubisHttpServlet.printError("unable to print region.", this);
      RubisHttpServlet.printException(e, this);
    }
  }

  //////////////////////////////////////
  //  Item related printed functions  //
  //////////////////////////////////////

  void printItemHeader()
  {
    out.println(
      "<table border=\"1\" summary=\"List of items\">"
        + "<thead>"
        + "<tr><th>Designation<TH>Price<TH>Bids<TH>End Date<th>Bid Now"
        + "<tbody>");
  }

  void printItem(
    String itemName,
    int itemId,
    float maxBid,
    int nbOfBids,
    String endDate)
  {
    try
    {
      out.println(
        "<tr><td><a href=\"edu.rice.rubis.servlets.ViewItem?itemId="
          + itemId
          + "\">"
          + itemName
          + "<td>"
          + maxBid
          + "<td>"
          + nbOfBids
          + "<td>"
          + endDate
          + "<td><a href=\"edu.rice.rubis.servlets.PutBidAuth?itemId="
          + itemId
          + "\"><img src=\"/rubis_servlets/bid_now.jpg\" height=\"22\" width=\"90\"></a>");
    }
    catch (Exception e)
    {
      RubisHttpServlet.printError("Unable to print item.", this);
      RubisHttpServlet.printException(e, this);
    }
  }

  void printItemFooter()
  {
    out.println("</table>");
  }

  void printItemFooter(String URLprevious, String URLafter)
  {
    out.println("</table>\n");
    out.println(
      "<p><center>\n"
        + URLprevious
        + "\n&nbsp&nbsp&nbsp"
        + URLafter
        + "\n</center>\n");
  }

  /**
   * Print the full description of an item and the bidding option if userId>0.
   */
  void printItemDescription(
    int itemId,
    String itemName,
    String description,
    float initialPrice,
    float reservePrice,
    float buyNow,
    int quantity,
    float maxBid,
    int nbOfBids,
    String sellerName,
    int sellerId,
    String startDate,
    String endDate,
    int userId,
    Connection conn)
  {
    PreparedStatement stmt = null;
    try
    {
      String firstBid;

      // Compute the current price of the item
      if (maxBid == 0)
      {
        firstBid = "none";
      }
      else
      {
        if (quantity > 1 && conn != null)
        {
          try
          {
            /* Get the qty max first bids and parse bids in this order
               until qty is reached. The bid that reaches qty is the
               current minimum bid. */

            stmt =
              conn.prepareStatement(
                "SELECT id,qty,max_bid FROM bids WHERE item_id=? ORDER BY bid DESC LIMIT ?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            stmt.setInt(1, itemId);
            stmt.setInt(2, quantity);
            ResultSet rs = stmt.executeQuery();
            if (rs.first())
            {
              int numberOfItems = 0;
              int qty;
              do
              {
                qty = rs.getInt("qty");
                numberOfItems = numberOfItems + qty;
                if (numberOfItems >= quantity)
                {
                  maxBid = rs.getFloat("max_bid");
                  ;
                  break;
                }
              }
              while (rs.next());
            }
          }
          catch (Exception e)
          {
            RubisHttpServlet.printError("Problem while computing current bid.", this);
            RubisHttpServlet.printException(e, this);
            return;
          }
        }
        Float foo = new Float(maxBid);
        firstBid = foo.toString();
      }
      if (userId > 0)
      {
        //printHTMLheader("RUBiS: Bidding\n");
        printHTMLHighlighted("You are ready to bid on: " + itemName);
      }
      else
      {
        //printHTMLheader("RUBiS: Viewing " + itemName + "\n");
        printHTMLHighlighted(itemName);
      }
      out.println(
        "<table>\n"
          + "<tr><td>Currently</td><td><b><big>"
          + maxBid
          + "</big></b>\n");
      // Check if the reservePrice has been met (if any)
      if (reservePrice > 0)
      { // Has the reserve price been met ?
        if (maxBid >= reservePrice)
          out.println("(The reserve price has been met)\n");
        else
          out.println("(The reserve price has NOT been met)\n");
      }
      out.println("</td></tr>");
      out.println(
        "<tr><td>Quantity</td><td><b><big>"
          + quantity
          + "</big></b></td></tr>\n"
          + "<tr><td>First bid</td><td><b><big>"
          + firstBid
          + "</big></b></td></tr>\n"
          + "<tr><td># of bids</td><td><b><big>"
          + nbOfBids
          + "</big></b> (<a href=\"/rubis_servlets/servlet/edu.rice.rubis.servlets.ViewBidHistory?itemId="
          + itemId
          + "\">bid history</a>)</td><td>\n"
          + "<tr><td>Seller</td><td><a href=\"/rubis_servlets/servlet/edu.rice.rubis.servlets.ViewUserInfo?userId="
          + sellerId
          + "\">"
          + sellerName
          + "</a> (<a href=\"/rubis_servlets/servlet/edu.rice.rubis.servlets.PutCommentAuth?to="
          + sellerId
          + "&itemId="
          + itemId
          + "\">Leave a comment on this user</a>)</td></tr>\n"
          + "<tr><td>Started</td><td>"
          + startDate
          + "\n"
          + "<tr><td>Ends</td><td>"
          + endDate
          + "\n"
          + "</td></tr></table>");
      // Can the user buy this item now ?
      if (buyNow > 0)
        out.println(
          "<p><a href=\"/rubis_servlets/servlet/edu.rice.rubis.servlets.BuyNowAuth?itemId="
            + itemId
            + "\">"
            + "<img src=\"/rubis_servlets/buy_it_now.jpg\" height=\"22\" width=\"150\"></a>"
            + "  <big><b>You can buy this item right now for only $"
            + buyNow
            + "</b></big><br /></p>\n");

      if (userId <= 0)
      {
        out.println(
          "<p><a href=\"/rubis_servlets/servlet/edu.rice.rubis.servlets.PutBidAuth?itemId="
            + itemId
            + "\"><img src=\"/rubis_servlets/bid_now.jpg\" height=\"22\" width=\"90\"> on this item</a></p>\n");
      }

      printHTMLHighlighted("Item description");
      out.println(description);
      out.println("<br />\n");

      if (userId > 0)
      {
        printHTMLHighlighted("Bidding");
        float minBid = maxBid + 1;
        printHTML(
          "<form action=\"/rubis_servlets/servlet/edu.rice.rubis.servlets.StoreBid\" method=\"POST\">\n"
            + "<input type=\"hidden\" name=\"minBid\" value=\" /"
            + minBid
            + "\">\n"
            + "<input type=\"hidden\" name=\"userId\" value=\" /"
            + userId
            + "\">\n"
            + "<input type=\"hidden\" name=\"itemId\" value=\" /"
            + itemId
            + "\">\n"
            + "<input type=\"hidden\" name=\"maxQty\" value=\" /"
            + quantity
            + "\">\n"
            + "<center><table>\n"
            + "<tr><td>Your bid (minimum bid is "
            + minBid
            + "):</td>\n"
            + "<td><input type=\"text\" size=\"10\" name=\"bid\" /></td></tr>\n"
            + "<tr><td>Your maximum bid:</td>\n"
            + "<td><input type=\"text\" size=\"10\" name=\"maxBid\" /></td></tr>\n");
        if (quantity > 1)
          printHTML(
            "<tr><td>Quantity:</td>\n"
              + "<td><input type=\"text\" size=\"5\" name=\"qty\" /></td></tr>\n");
        else
          printHTML("<input type=\"hidden\" name=\"qty\" value=\"1\" />\n");
        printHTML("</table></center><p><input type=\"submit\" value=\"Bid now!\" /></p></form>\n");
      }
    }
    catch (Exception e)
    {
      RubisHttpServlet.printError("Unable to print item description.", this);
      RubisHttpServlet.printException(e, this);
    }
  }

  ////////////////////////////////////////
  // About me related printed functions //
  ////////////////////////////////////////

  void printUserBidsHeader()
  {
    printHTMLHighlighted("<p><h3>Items you have bid on.</h3></p>\n");
    out.println(
      "<table border=\"1\" summary=\"Items You've bid on\">\n"
        + "<thead>\n"
        + "<tr><th>Designation</th><th>Initial Price</th><th>Current price</th><th>Your max bid</th><th>Quantity"
        + "</th><th>Start Date</th><th>End Date<TH>Seller</th><th>Put a new bid\n"
        + "</thead><tbody>\n");
  }

  void printItemUserHasBidOn(
    int itemId,
    String itemName,
    float initialPrice,
    int quantity,
    String startDate,
    String endDate,
    int sellerId,
    String sellerName,
    float currentPrice,
    float maxBid,
    String username,
    String password)
  {
    try
    {
      out.println(
        "<tr><td><a href=\"/rubis_servlets/servlet/edu.rice.rubis.servlets.ViewItem?itemId="
          + itemId
          + "\">"
          + itemName
          + "</a></td><td>"
          + initialPrice
          + "</td><td>"
          + currentPrice
          + "</td><td>"
          + maxBid
          + "</td><td>"
          + quantity
          + "</td><td>"
          + startDate
          + "</td><td>"
          + endDate
          + "</td><td><a href=\"/rubis_servlets/servlet/edu.rice.rubis.servlets.ViewUserInfo?userId="
          + sellerId
          + "\">"
          + sellerName
          + "</a></td><td><a href=\"/rubis_servlets/servlet/edu.rice.rubis.servlets.PutBid?itemId="
          + itemId
          + "&nickname="
          + username
          + "&password="
          + password
          + "\"><img src=\"/rubis_servlets/bid_now.jpg\" height=22 width=90></a></td></tr>\n");
    }
    catch (Exception e)
    {
      RubisHttpServlet.printError("Unable to print item.", this);
      RubisHttpServlet.printException(e, this);
    }
  }

  void printUserWonItemHeader()
  {
    printHTML("<br />");
    printHTMLHighlighted("<h3>Items you won in the past 30 days.</h3>\n");
    out.println(
      "<table border=\"1\" summary=\"List of items\">\n"
        + "<thead>\n"
        + "<tr><th>Designation</th><th>Price you bought it</th><th>Seller</th></tr></thead>"
        + "<tbody>\n");
  }

  void printUserWonItem(
    int itemId,
    String itemName,
    float currentPrice,
    int sellerId,
    String sellerName)
  {
    try
    {
      out.println(
        "<tr><td><a href=\"/rubis_servlets/servlet/edu.rice.rubis.servlets.ViewItem?itemId="
          + itemId
          + "\">"
          + itemName
          + "</a>\n"
          + "</td><td>"
          + currentPrice
          + "\n"
          + "</td><td><a href=\"/rubis_servlets/servlet/edu.rice.rubis.servlets.ViewUserInfo?userId="
          + sellerId
          + "\">"
          + sellerName
          + "</a></td>\n");
    }
    catch (Exception e)
    {
      RubisHttpServlet.printError("Unable to print item.", this);
      RubisHttpServlet.printException(e, this);
    }
  }

  void printUserBoughtItemHeader()
  {
    printHTML("<br />");
    printHTMLHighlighted("<h3>Items you bouhgt in the past 30 days.</h3>\n");
    out.println(
      "<table border=\"1\" summary=\"List of items\">\n"
        + "<thead>\n"
        + "<tr><th>Designation</th><th>Quantity</th><th>Price you bought it</th><th>Seller</th></tr></thead>"
        + "<tbody>\n");
  }

  void printUserBoughtItem(
    int itemId,
    String itemName,
    float buyNow,
    int quantity,
    int sellerId,
    String sellerName)
  {
    try
    {
      out.println(
        "<tr><td><a href=\"/rubis_servlets/servlet/edu.rice.rubis.servlets.ViewItem?itemId="
          + itemId
          + "\">"
          + itemName
          + "</a>\n"
          + "</td><td>"
          + quantity
          + "\n"
          + "</td><td>"
          + buyNow
          + "\n"
          + "</td><td>s<a href=\"/rubis_servlets/servlet/edu.rice.rubis.servlets.ViewUserInfo?userId="
          + sellerId
          + "\">"
          + sellerName
          + "</a></td>\n");
    }
    catch (Exception e)
    {
      RubisHttpServlet.printError("Unable to print item.", this);
      RubisHttpServlet.printException(e, this);
    }
  }

  void printSellHeader(String title)
  {
    printHTMLHighlighted("<h3>" + title + "</h3>\n");
    out.println(
      "<table border=\"1\" summary=\"List of items\">\n"
        + "<thead>\n"
        + "<tr><th>Designation</th><th>Initial Price</th><th>Current price</th><th>Quantity</th><th>ReservePrice</th><th>Buy Now"
        + "</th><th>Start Date</th><th>End Date</th></tr></thead>\n"
        + "<tbody>\n");
  }

  void printSell(
    int itemId,
    String itemName,
    float initialPrice,
    float reservePrice,
    int quantity,
    float buyNow,
    String startDate,
    String endDate,
    float currentPrice)
  {
    try
    {
      out.println(
        "<tr><td><a href=\"/rubis_servlets/servlet/edu.rice.rubis.servlets.ViewItem?itemId="
          + itemId
          + "\">"
          + itemName
          + "</a></td><td>"
          + initialPrice
          + "</td><td>"
          + currentPrice
          + "</td><td>"
          + quantity
          + "</td><td>"
          + reservePrice
          + "</td><td>"
          + buyNow
          + "</td><td>"
          + startDate
          + "</td><td>"
          + endDate
          + "</td></tr>\n");
    }
    catch (Exception e)
    {
      RubisHttpServlet.printError("Unable to print item.", this);
      RubisHttpServlet.printException(e, this);
    }
  }

  ///////////////////////////////////////
  // Buy now related printed functions //
  ///////////////////////////////////////

  /**
   * Print the full description of an item and the buy now option
   *
   * @param item an <code>Item</code> value
   * @param userId an authenticated user id
   */
  void printItemDescriptionToBuyNow(
    int itemId,
    String itemName,
    String description,
    float buyNow,
    int quantity,
    int sellerId,
    String sellerName,
    String startDate,
    String endDate,
    int userId)
  {
    try
    {

      //printHTMLheader("RUBiS: Buy Now");
      printHTMLHighlighted("You are ready to buy this item: " + itemName);

      out.println(
        "<table>\n"
          + "<tr><td>Quantity</td><td><b><big>"
          + quantity
          + "</big></b></td></tr>\n"
          + "<tr><td>Seller</td><td><a href=\"/rubis_servlets/servlet/edu.rice.rubis.servlets.ViewUserInfo?userId="
          + sellerId
          + "\">"
          + sellerName
          + "</a> (<a href=\"/rubis_servlets/servlet/edu.rice.rubis.servlets.PutCommentAuth?to="
          + sellerId
          + "&itemId="
          + itemId
          + "\">Leave a comment on this user</a>)</td></tr>\n"
          + "<tr><td>Started</td><td>"
          + startDate
          + "\n"
          + "<tr><td>Ends</td><td>"
          + endDate
          + "</td></tr>\n"
          + "</table>");

      printHTMLHighlighted("Item description");
      out.println(description);
      out.println("<br /><p>\n</p>");

      printHTMLHighlighted("Buy Now");
      printHTML(
        "<form action=\"/rubis_servlets/servlet/edu.rice.rubis.servlets.StoreBuyNow\" method=\"POST\">\n"
          + "<input type=\"hidden\" name=\"userId\" value="
          + userId
          + " />\n"
          + "<input type=\"hidden\" name=\"itemId\" value="
          + itemId
          + " />\n"
          + "<input type=\"hidden\" name=\"maxQty\" value="
          + quantity
          + " />\n");
      if (quantity > 1)
        printHTML(
          "<center><table><tr><td>Quantity:</td>\n"
            + "<td><input type=\"text\" size=\"5\" name=\"qty\" /></td></tr></table></center>\n");
      else
        printHTML("<input type=\"hidden\" name=\"qty\" value=\"1\" />\n");
      printHTML("<p><center><input type=\"submit\" value=\"Buy now!\" /></center><p>\n");
    }
    catch (Exception e)
    {
      RubisHttpServlet.printError("Unable to print item description.", this);
      RubisHttpServlet.printException(e, this);
    }
  }

  ///////////////////////////////////
  // Bid related printed functions //
  ///////////////////////////////////

  void printBidHistoryHeader()
  {
    out.println(
      "<table border=\"1\" summary=\"List of bids\">"
        + "<thead>"
        + "<tr><th>User ID</th><th>Bid amount</th><th>Date of bid</th></tr>"
        + "<tbody>");
  }

  void printBidHistoryFooter()
  {
    out.println("</tbody></table>");
  }

  void printBidHistory(int userId, String bidderName, float bid, String date)
  {
    try
    {
      out.println(
        "<tr><td><a href=\"edu.rice.rubis.servlets.ViewUserInfo?userId="
          + userId
          + "\">"
          + bidderName
          + "/<td><td>"
          + bid
          + "</td><td>"
          + date + "</td></tr>");
    }
    catch (Exception e)
    {
      RubisHttpServlet.printError("Unable to print bid.", this);
      RubisHttpServlet.printException(e, this);
    }
  }

  /////////////////////////////////////////
  //  Comment related printed functions  //
  /////////////////////////////////////////

  void printCommentHeader()
  {
    out.println("<dl>");
  }

  void printComment(String userName, int userId, String date, String comment)
  {
    try
    {
      out.println(
        "<DT><b><big><a href=\"edu.rice.rubis.servlets.ViewUserInfo?userId="
          + userId
          + "\">"
          + userName
          + "</a></big></b>"
          + " wrote the "
          + date
          + "<dd><i>"
          + comment
          + "</i></dd>");
    }
    catch (Exception e)
    {
      RubisHttpServlet.printError("Unable to print comment.", this);
      RubisHttpServlet.printException(e, this);
    }
  }

  void printCommentFooter()
  {
    out.println("</dl>");
  }

}
