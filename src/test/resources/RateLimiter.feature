Feature: Notification service should rate limit based on user and notification type

  Scenario: Successfully send one notification without limit
    Given A notification with type "NEWS" and user id "USER_ID_1"
    When Trying to send notification
    Then Notification is sent succesfully 1 times

  Scenario: Successfully send two notification with limit of one per day
    Given A notification with type "STATUS" and user id "USER_ID_1"
    When Trying to send notification
    Then Notification is sent succesfully 2 times

  Scenario: Successfully send two notification with limit of one per day
    Given A notification with type "MARKETING" and user id "USER_ID_1"
    When Trying to send notification
    Then Notification is sent succesfully 3 times

  Scenario: Fail to send 4 notifications for news rate limit policy
    Given A notification with type "NEWS" and user id "USER_ID_2"
    When Trying to send notification
    Then Notification is not sent succesfully for type "NEWS" and user id "USER_ID_2" after sending 1 previously

  Scenario: Fail to send 3 notification for status rate limit policy
    Given A notification with type "STATUS" and user id "USER_ID_3"
    When Trying to send notification
    Then Notification is not sent succesfully for type "STATUS" and user id "USER_ID_3" after sending 2 previously

  Scenario: Fail to send one more notification for marketing rate limit policy
    Given A notification with type "MARKETING" and user id "USER_ID_4"
    When Trying to send notification
    Then Notification is not sent succesfully for type "MARKETING" and user id "USER_ID_4" after sending 3 previously
