from django.shortcuts import render
from django.http import HttpResponse
from . import externals


def home(request):
    return render(request, 'stockDashboard/app.html')


def dashboard(request):
    inputData = request.POST['input']
    scriptName, scriptSector = externals.stockName(inputData)
    context = {
        'scriptName': scriptName,
        'scriptSector': scriptSector
    }
    return render(request, 'stockDashboard/dashboard.html', context)
