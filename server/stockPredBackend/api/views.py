from django.shortcuts import render
from django.views.generic import View
from django.http import JsonResponse
import yfinance as yf
from datetime import datetime, timedelta


class stockDataView(View):
    def get(self, request, symbol):
        end_date = datetime.now().date()
        start_date = end_date - timedelta(days=59)
        data = yf.download(symbol, interval='30m', start=start_date, end=end_date)
        data = data.reset_index()
        return JsonResponse(data.to_json(orient='records'), safe=False)
