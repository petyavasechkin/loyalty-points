# Read Me First
 LoyaltyPoints Service Project
 
 Usage: 
    POST /transaction                       Transaction for customer
       Request body example: {
                               "id": 30,
                               "customerId": 1,
                               "amount": 7800,
                               "dateTime": "2021-03-10T00:16:18"
                             }
  
    GET /loyal-points/{id}                  Fetching available and pending points for customer with Id
    GET /loyal-points/{id}/history          Fetching accrued and spent points history for customer with Id
    PUT /loyal-points/{id}/points/{amount}  Trying to spent euro {amount} for customer with Id



