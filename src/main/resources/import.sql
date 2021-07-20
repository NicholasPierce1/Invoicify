INSERT INTO APP_USER(id,username,password) VALUES(1,'admin1','password1');

INSERT INTO COMPANY VALUES(1, 'Subway');
INSERT INTO COMPANY VALUES(2, 'KFC');
INSERT INTO COMPANY VALUES(3, 'Whataburger');

INSERT INTO FLAT_FEE_BILLING_RECORD VALUES(1, 1, 5, 'flat fee billing record one desc', true, 150.56);
INSERT INTO FLAT_FEE_BILLING_RECORD VALUES(2, 1, 5, 'flat fee billing record two desc', true, 987.65);
INSERT INTO FLAT_FEE_BILLING_RECORD VALUES(3, 1, 5, 'flat fee billing record three desc', false, 123.78);

INSERT INTO RATE_BASED_BILLING_RECORD VALUES(4, 1, 5, 'rate base billing record one desc', true, 5, 13);
INSERT INTO RATE_BASED_BILLING_RECORD VALUES(5, 1, 5, 'rate base billing record two desc', false, 10, 3.56);
INSERT INTO RATE_BASED_BILLING_RECORD VALUES(6, 1, 5, 'rate base billing record three desc', false, 7, 100);