import requests
import json
import csv

fields = ['symbol', 'date', 'lastPrice', 'change', 'previousClose', 'open', 'max', 'min']
headerNSE= {
    'accept': '*/*',
    'accept-encoding': 'gzip, deflate, br',
    'accept-language': 'en-US,en;q=0.9',
    'cookie': '_gid=GA1.2.252554547.1682359589; _ga=GA1.1.2019465461.1682057461; nseQuoteSymbols=[{"symbol":"","identifier":null,"type":"equity"},{"symbol":"ITC","identifier":null,"type":"equity"}]; AKA_A2=A; nsit=w3KyCWY-b7hynQr4EdXu5Tqm; bm_mi=18042C5339BEA2C87499913A84DB4383~YAAQP0o5FzcjDaKHAQAA6iecthPPXM8NoEjRJha3wBiMmipi7MnW+/vOWhsxnClGPQb2weNXKGnStDiptFlRU/5PAVskLM11lUN6KDbfHI6+JcDufEdqH7uCUgkR2SmVViV9W7mSZvvb3nViwk4j8yYyhECtV5AmtmTQEE/Vt3ElUX6fPBWK7a4j+UQjYA6hLlyBSnUnZgIuhK4fu2C8bP+x8bAglIbqdaqLP6NhGJekanPVPCAtCTEhyVEip6FBIWbN8XjqfYHhbObRnOnMHB2525edtHHu0855utKasNCASpmxILHQwjVaAYs8d6Pbvb2Kp90KeA/hIf2KHkzPdGI=~1; defaultLang=en; ak_bmsc=534A6320233B15DA69450EF243165E61~000000000000000000000000000000~YAAQP0o5F14jDaKHAQAAQy2cthMqkNS5uwvOOi9iBPJ83aDH6GZ9zI/8ro+sCQt899bsXmILtwO3IGdrAH+rYeAUzPlKm/dTEQy7QYAvw7Nw/poKNCmX53wmKC6G4ZBA8ymszRuGjEr75E0H805NeoWwhpLWd2Q6b+un9GVs2aBllF/mIE9btviMX6932I6HlHHDSqMkjVxTOxKrSJa+pmbfqwYM04zcQnG1jzyU2P8oPJ16sG/3VPfeAYnIsbXJgogJn6sKNTzhGBm1UqL/+3tGaWCNzTElDmQ8L5x5W8JFiXLNypx9pdlC1wsJSt8x9I0oIWLOdfyxhkXRT5KL0MQ9EQlJjlq55cvwzkYpVy6GrnWjbktOOYmDMBlLtZNCLM6llcvKc5WakXY3IKws4kXpzAJLSo14Aq+Z4z1Ws7dPsKNLSdEpRDR70T83rMcH; nseappid=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJhcGkubnNlIiwiYXVkIjoiYXBpLm5zZSIsImlhdCI6MTY4MjM5NjA2NiwiZXhwIjoxNjgyNDAzMjY2fQ.bRlRj7HvX59bu__ZKGuQw09vfHrcVTpkzb0fVLeQk8U; RT="z=1&dm=nseindia.com&si=bc07c69a-c5fd-4bea-9b71-21e9adc1d344&ss=lgvr2w61&sl=1&tt=16x&bcn=%2F%2F684d0d47.akstat.io%2F"; _ga_PJSKY6CFJH=GS1.1.1682395900.8.1.1682396066.60.0.0; bm_sv=55BAB09450B71509EFCB26A5D70B7067~YAAQT0o5F9HrIaGHAQAAfrWethO+6bJfn8bzOfIvn66f2W1KVUozrBlnKuQULAowCcoykHswDHrlpLs7BGTDXuTsx/1yZlLVbfFd6o1+f8l62QjGoU2leiQXpp5SRy0lgaljLEFdA2wO5WlyuacsleYI95jLL5qbzp3xbAJaj/UjacYHrC/rTw52fCTixNU4RSZB5lS+Ij8amysXRpunF5zs2t8MvAo4RwsOBQBNAy53PZXcZ05xAkiRCqlcvJP0bkiW~1',
    'user-agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/112.0.0.0 Safari/537.36'
}
parameters = {
    'function': 'TIME_SERIES_INTRADAY',
    'symbol': 'ITC.NSE',
    'apikey': 'LWF9OWJW8G8FG8FI',
    'datatype': 'json',
    'interval': '30min'
}


response = requests.get('https://www.alphavantage.co/query', params=parameters)
if response.status_code == 200:
    print("Request successful!")
else:
    print("Request failed with status code:", response.status_code)


jsonData = json.loads(response.text)
print(response.text)

"""
with open('../api/ITCdata.csv', 'a', newline='') as z:
    writer = csv.DictWriter(z, fieldnames=fields)
    writer.writerow({
        'symbol': jsonData['info']['symbol'],
        'date': jsonData['metadata']['lastUpdateTime'],
        'lastPrice': jsonData['priceInfo']['lastPrice'],
        'change': jsonData['priceInfo']['change'],
        'previousClose': jsonData['priceInfo']['previousClose'],
        'open': jsonData['priceInfo']['open'],
        'max': jsonData['priceInfo']['intraDayHighLow']['max'],
        'min': jsonData['priceInfo']['intraDayHighLow']['min']
    })
    """

print(f"Data written to CSV file: {jsonData}")