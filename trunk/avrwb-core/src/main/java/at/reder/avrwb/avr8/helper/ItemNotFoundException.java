/*
 * $Id$
 *
 * Copyright (C) 2015 Wolfgang Reder <wolfgang.reder@aon.at>.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 *
 */
package at.reder.avrwb.avr8.helper;

import java.util.logging.Level;
import org.openide.util.NbBundle;

/**
 *
 * @author Wolfgang Reder
 */
@NbBundle.Messages({"# {0} - itemName",
                    "# {1} - deviceName",
                    "DeviceImpl_msg_item_not_found=Cannot find item with name {0} in device {1}."})
public class ItemNotFoundException extends Exception
{

  public static void processItemNotFound(String deviceName,
                                         String itemName,
                                         NotFoundStrategy nfStrategy) throws ItemNotFoundException
  {
    switch (nfStrategy) {
      case IGNORE:
        return;
      case WARNING:
        AVRWBDefaults.LOGGER.log(Level.WARNING, Bundle.DeviceImpl_msg_item_not_found(itemName, deviceName));
        break;
      case ERROR:
      default:
        AVRWBDefaults.LOGGER.log(Level.WARNING, Bundle.DeviceImpl_msg_item_not_found(itemName, deviceName));
        throw new ItemNotFoundException(Bundle.DeviceImpl_msg_item_not_found(itemName, deviceName));
    }

  }

  public ItemNotFoundException(String item)
  {
    super(item);
  }

}
