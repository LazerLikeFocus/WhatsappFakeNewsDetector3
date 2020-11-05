# -*- coding: utf-8 -*-
"""
Created on Fri Oct 30 12:31:15 2020

@author: Abhishek
"""

import numpy
import pickle
#import nltk
import string
#from nltk.corpus import stopwords
#from tensorflow.keras.preprocessing.text import Tokenizer
from tensorflow.keras.preprocessing.sequence import pad_sequences
from os.path import dirname, join
filename_pos = join(dirname(__file__), "tokenizer.pickle")

maxlength = 42
#maxlength = 500

def find(X):
    X =  X.split()
    table = str.maketrans('', '', string.punctuation)
    X = [w.translate(table) for w in X]
    X = [word for word in X if word.isalpha()]
    #stop_words = set(stopwords.words('english'))
    #X = [w for w in X if not w in stop_words]
    X = [word for word in X if len(word) > 1]
        
    #tokenizer = Tokenizer()

    # loading
    with open(filename_pos, 'rb') as handle:
        tokenizer = pickle.load(handle)

    #tokenizer.fit_on_texts(X)
    
    encoded_docs = tokenizer.texts_to_sequences(X)
    
    Xtest = pad_sequences(encoded_docs, maxlen=maxlength, padding='post')

    Xtest = Xtest[:,0]

    Xtest = numpy.pad(Xtest, (0, maxlength - Xtest.shape[0]), 'constant')

    Xtest = Xtest.reshape(1, Xtest.shape[0])
    
    return Xtest

# s = 'Provided to YouTube by Saregama India Ltd Mann Ki Lagan · Rahat Fateh Ali Khan Ek Cup Love℗ 2020 Saregama India LtdReleased on: 1969-09-27'
# print(find(s))

