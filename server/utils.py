import io
from tensorflow import keras
from sklearn.preprocessing import MinMaxScaler
import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
from flask import Response


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


def frontendPlot(companyName, realStockPrice, predictedPrice):
    plotView(companyName, realStockPrice, predictedPrice)
    buffer= io.BytesIO()
    plt.savefig(buffer, format='png')
    buffer.seek(0)
    plt.clf()
    return Response(buffer.getvalue(), mimetype='image/png')


loadDS('ITC.NS')
