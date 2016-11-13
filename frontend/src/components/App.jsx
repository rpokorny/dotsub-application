import React from 'react';
import './App.css';

import UploadForm from './UploadForm';
import FileList from './FileList';

export default ({model}) =>
    <main>
        <UploadForm model={model.uploadModel} />
        <FileList files={model.files} />
    </main>;
