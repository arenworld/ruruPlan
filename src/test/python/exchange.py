import FinanceDataReader as fdr

# USD/KRW 환율 정보 가져오기
exchange_rate = fdr.DataReader('JPY/KRW').iloc[-1, 0]
print(exchange_rate)