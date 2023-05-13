from django.urls import path
from . import views

urlpatterns = [
    path('query/<str:symbol>/', views.stockDataView.as_view())
]