package edu.rice.rubis.servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** This servlets display the page allowing a user to put a bid
 * on an item.
 * It must be called this way :
 * <pre>
 * http://..../PutBid?itemId=xx&nickname=yy&password=zz
 *    where xx is the id of the item
 *          yy is the nick name of the user
 *          zz is the user password
 * /<pre>
 * @author <a href="mailto:cecchet@rice.edu">Emmanuel Cecchet</a> and <a href="mailto:julie.marguerite@inrialpes.fr">Julie Marguerite</a>
 * @version 1.0
 */

public class PutBid extends RubisHttpServlet
{
 

  public int getPoolSize()
  {
    return Config.PutBidPoolSize;
  }

  public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException
  {
     ServletPrinter sp = null;
     
    String itemStr = request.getParameter("itemId");
    String name = request.getParameter("nickname");
    String pass = request.getParameter("password");
    sp = new ServletPrinter(response, "PubBid");
    sp.printHTMLheader("RUBiS: Put bid"); 

    if ((itemStr == null)
      || (itemStr.equals(""))
      || (name == null)
      || (name.equals(""))
      || (pass == null)
      || (pass.equals("")))
    {
      printError("Item id, name and password are required - Cannot process the request.", sp);
      sp.printHTMLfooter();
      return;
    }
    Integer itemId = new Integer(itemStr);
    
    PreparedStatement stmt = null;
    Connection conn = null;
    conn = getConnection();
    // Authenticate the user who want to bid
    Auth auth = new Auth(conn, sp);
    int userId = auth.authenticate(name, pass);
    if (userId == -1)
    {
      printError(" You don't have an account on RUBiS! You have to register first.", sp);
      sp.printHTMLfooter();
      closeConnection(stmt, conn);
      return;
    }

    // Try to find the Item corresponding to the Item ID
    String itemName, endDate, startDate, description, sellerName;
    float maxBid, initialPrice, buyNow, reservePrice;
    int quantity, sellerId, nbOfBids = 0;
    ResultSet rs = null;
    try
    {
      stmt = conn.prepareStatement("SELECT * FROM items WHERE id=?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
      stmt.setInt(1, itemId.intValue());
      rs = stmt.executeQuery();
    }
    catch (Exception e)
    {
      printError("Failed to execute Query for item.", sp);
      printException(e, sp);
      sp.printHTMLfooter();
      closeConnection(stmt, conn);
      return;
    }
    try
    {
      if (!rs.first())
      {
        printError("This item does not exist!", sp);
        sp.printHTMLfooter();
        closeConnection(stmt, conn);
        return;
      }
      itemName = rs.getString("name");
      description = rs.getString("description");
      endDate = rs.getString("end_date");
      startDate = rs.getString("start_date");
      initialPrice = rs.getFloat("initial_price");
      reservePrice = rs.getFloat("reserve_price");
      buyNow = rs.getFloat("buy_now");
      quantity = rs.getInt("quantity");
      sellerId = rs.getInt("seller");
      
      PreparedStatement sellerStmt = null;
      try
      {
        sellerStmt =
          conn.prepareStatement("SELECT nickname FROM users WHERE id=?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        sellerStmt.setInt(1, sellerId);
        ResultSet sellerResult = sellerStmt.executeQuery();
        // Get the seller's name		 
        if (sellerResult.first())
          sellerName = sellerResult.getString("nickname");       
        else
        {
          printError("Unknown seller.", sp);
          sp.printHTMLfooter();
          sellerStmt.close();
          closeConnection(stmt, conn);
          return;
        }
        sellerStmt.close();

      }
      catch (Exception e)
      {
        printError("Failed to executeQuery for seller.", sp);
        printException(e, sp);
        sp.printHTMLfooter();
        sellerStmt.close();
        closeConnection(stmt, conn);
        return;
      }
      PreparedStatement maxBidStmt = null;
      try
      {
        maxBidStmt =
          conn.prepareStatement(
            "SELECT MAX(bid) AS bid FROM bids WHERE item_id=?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        maxBidStmt.setInt(1, itemId.intValue());
        ResultSet maxBidResult = maxBidStmt.executeQuery();
        // Get the current price (max bid)		 
        if (maxBidResult.first())
          maxBid = maxBidResult.getFloat("bid");
        else
          maxBid = initialPrice;
        maxBidStmt.close();
      }
      catch (Exception e)
      {
        printError("Failed to executeQuery for max bid.", sp);
        printException(e, sp);
        sp.printHTMLfooter();
        maxBidStmt.close();
        closeConnection(stmt, conn);
        return;
      }
      PreparedStatement nbStmt = null;
      try
      {
        nbStmt =
          conn.prepareStatement(
            "SELECT COUNT(*) AS bid FROM bids WHERE item_id=?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        nbStmt.setInt(1, itemId.intValue());
        ResultSet nbResult = nbStmt.executeQuery();
        // Get the number of bids for this item
        if (nbResult.first())
          nbOfBids = nbResult.getInt("bid");
        nbStmt.close();
      }
      catch (Exception e)
      {
        printError("Failed to executeQuery for number of bids.", sp);
        printException(e, sp);
        sp.printHTMLfooter();
        nbStmt.close();
        closeConnection(stmt, conn);
        return;
      }
      sp.printItemDescription(
        itemId.intValue(),
        itemName,
        description,
        initialPrice,
        reservePrice,
        buyNow,
        quantity,
        maxBid,
        nbOfBids,
        sellerName,
        sellerId,
        startDate,
        endDate,
        userId,
        conn);
    }
    catch (Exception e)
    {
      printError("Exception getting item list.", sp);
      printException(e, sp);
      sp.printHTMLfooter();
      closeConnection(stmt, conn);
      return;
    }
    closeConnection(stmt, conn);
    sp.printHTMLfooter();
  }

  public void doPost(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException
  {
    doGet(request, response);
  }

  /**
  * Clean up the connection pool.
  */
  public void destroy()
  {
    super.destroy();
  }
}
