from tensorflow import keras
from sklearn.preprocessing import MinMaxScaler
import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
import yfinance as yf
import datetime
from apscheduler.schedulers.background import BackgroundScheduler
from apscheduler.triggers.interval import IntervalTrigger


def dataRetrieval(symbol):
    data = yf.download(tickers=symbol, interval="30m", period="1d")
    with open(file=f'../dataset/{symbol}.csv', mode='a', newline='') as dataUpdated:
        data.to_csv(dataUpdated, header=dataUpdated.tell() == 0)
    print('Data retrieved & Updated at', datetime.datetime.now().strftime('%Y-%m-%d %H:%M:%S'))
    return data


def automaticUpdation(func):
    scheduler = BackgroundScheduler()
    scheduler.add_job(func={func}, trigger=IntervalTrigger(minutes=30),
                      start_date=datetime.datetime.now().replace(hour=9, minute=15, second=0),
                      end_date=datetime.datetime.now().replace(hour=15, minute=30, second=0), timezone='Asia/Kolkata')
    scheduler.start()


def loadDS(companyName):
    scaler = MinMaxScaler(feature_range=(0, 1))
    model, df, testDF = loadEssentials(companyName=companyName)
    xTEST, realSP = dataProcessing(df, testDF, scaler=scaler)

    pred = model.predict(xTEST)
    pred = scaler.inverse_transform(pred)

    plotView(companyName=companyName, realStockPrice=realSP, predictedPrice=pred)
    return print(df.head(5))


def loadEssentials(companyName):
    model = keras.models.load_model(f"../model/exports/{companyName}/")
    df = pd.read_csv(f'../dataset/{companyName}-train.csv')
    testDF = pd.read_csv(f'../dataset/{companyName}-test.csv')
    return model, df, testDF


def dataProcessing(df, testDF, scaler):
    trainingSetScaled = scaler.fit_transform(df['Close'].values.reshape(-1, 1))
    realSP = testDF['Close'].values
    dfTotal = pd.concat((df['Open'], testDF['Open']), axis=0)
    modelInp = dfTotal[len(dfTotal) - len(testDF) - 60:].values
    modelInp = modelInp.reshape(-1, 1)
    modelInp = scaler.transform(modelInp)
    xTEST = []
    for i in range(60, len(modelInp)):
        xTEST.append(modelInp[i - 60:i, 0])
    xTEST = np.array(xTEST)
    xTEST = np.reshape(xTEST, newshape=(xTEST.shape[0], xTEST.shape[1], 1))
    return xTEST, realSP


def plotView(companyName, realStockPrice, predictedPrice):
    plt.plot(realStockPrice, color='r', label='real')
    plt.plot(predictedPrice, color='b', label='pred')
    plt.title(f'{companyName} Stock Prediction')
    plt.ylabel('Stock prices')
    plt.xlabel('Time')
    plt.legend()
    plt.show()

