import json

import numpy as np
import pandas as pd
from django.shortcuts import render
from django.views.generic import View
from django.http import JsonResponse
import yfinance as yf
from datetime import datetime, timedelta
from sklearn.preprocessing import MinMaxScaler
import tensorflow as tf
from tensorflow import keras


class stockDataView(View):
    def get(self, request, symbol):
        end_date = datetime.now().date()
        start_date = end_date - timedelta(days=45)
        end_date1 = datetime.now().date()
        start_date1 = datetime.now().date() - timedelta(days=14)

        df = yf.download(symbol, interval='30m', start=start_date, end=end_date)
        df = df.reset_index()
        trainSet = df.iloc[:, 1:2].values
        scaler = MinMaxScaler(feature_range=(0, 1))
        trainingSetScaled = scaler.fit_transform(df['Close'].values.reshape(-1, 1))

        xTRAIN = []
        yTRAIN = []
        for i in range(60, len(trainingSetScaled)):
            xTRAIN.append(trainingSetScaled[i - 60:i, 0])
            yTRAIN.append(trainingSetScaled[i, 0])
        xTRAIN, yTRAIN = np.array(xTRAIN), np.array(yTRAIN)

        xTRAIN = np.reshape(xTRAIN, newshape=(xTRAIN.shape[0], xTRAIN.shape[1], 1))
        regressor = keras.models.Sequential([
            keras.layers.LSTM(units=64, return_sequences=True, input_shape=(xTRAIN.shape[1], 1)),
            keras.layers.Dropout(rate=0.2),
            keras.layers.LSTM(units=50, return_sequences=True),
            keras.layers.Dropout(rate=0.2),
            keras.layers.LSTM(units=50),
            keras.layers.Dropout(rate=0.2),
            keras.layers.Dense(units=1)
        ])

        regressor.compile(optimizer='adam', loss=keras.losses.mean_squared_error)
        regressor.fit(x=xTRAIN, y=yTRAIN, batch_size=32, epochs=5)

        # TEST DATA
        testDF = yf.download('ITC.NS', interval='30m', start=start_date1, end=end_date1)
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
        pred = regressor.predict(xTEST)
        pred = scaler.inverse_transform(pred)
        # making prediction

        realData = [modelInp[len(modelInp) + 1 - 60:len(modelInp + 1), 0]]
        realData = np.array(realData)
        realData = np.reshape(realData, newshape=(realData.shape[0], realData.shape[1], 1))
        prediction = regressor.predict(realData)
        prediction = scaler.inverse_transform(prediction)
        print(prediction)
        return JsonResponse({'prediction': prediction.tolist()})
