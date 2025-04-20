CREATE TABLE TRANSACTIONS
(
  ID                 BIGINT PRIMARY KEY                     NOT NULL AUTO_INCREMENT,
  OPERATION_ID       VARCHAR UNIQUE                         NOT NULL,
  OPERATION_AMOUNT   DECIMAL                                NOT NULL,
  OPERATION_CURRENCY VARCHAR(3)                             NOT NULL,
  ACCOUNT_ID         BIGINT                                 NOT NULL,
  VALUE_DATE         DATE                                   NOT NULL,
  CREATED_AT         TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL,
  FOREIGN KEY (ACCOUNT_ID) REFERENCES ACCOUNTS (ID)
);
