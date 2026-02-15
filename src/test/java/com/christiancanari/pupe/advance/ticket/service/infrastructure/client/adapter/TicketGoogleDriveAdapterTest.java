package com.christiancanari.pupe.advance.ticket.service.infrastructure.client.adapter;

import com.christiancanari.pupe.advance.ticket.service.domain.model.TicketFile;
import com.christiancanari.pupe.advance.ticket.service.infrastructure.web.exception.CoreTechnicalException;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TicketGoogleDriveAdapterTest {

    @Mock
    private Drive drive;

    @Mock
    private Drive.Files driveFiles;

    @Mock
    private Drive.Files.List driveFilesList;

    @Mock
    private Drive.Files.Get driveFilesGet;

    private TicketGoogleDriveAdapter adapter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        adapter = new TicketGoogleDriveAdapter(drive);
    }

    // ----------------------------------------------------------------------
    // findFolderIdByName
    // ----------------------------------------------------------------------

    @Test
    @DisplayName("Debe retornar el id de la carpeta cuando existe")
    void shouldReturnFolderIdWhenFolderExists() throws Exception {

        File folder = new File();
        folder.setId("folder-id");

        FileList fileList = new FileList();
        fileList.setFiles(List.of(folder));

        when(drive.files()).thenReturn(driveFiles);
        when(driveFiles.list()).thenReturn(driveFilesList);
        when(driveFilesList.setQ(anyString())).thenReturn(driveFilesList);
        when(driveFilesList.setFields(anyString())).thenReturn(driveFilesList);
        when(driveFilesList.setSupportsAllDrives(true)).thenReturn(driveFilesList);
        when(driveFilesList.setIncludeItemsFromAllDrives(true)).thenReturn(driveFilesList);
        when(driveFilesList.execute()).thenReturn(fileList);

        Optional<String> result = adapter.findFolderIdByName("Carpeta");

        assertTrue(result.isPresent());
        assertEquals("folder-id", result.get());
    }

    @Test
    @DisplayName("Debe retornar empty cuando la carpeta no existe")
    void shouldReturnEmptyWhenFolderNotFound() throws Exception {

        FileList fileList = new FileList();
        fileList.setFiles(List.of());

        when(drive.files()).thenReturn(driveFiles);
        when(driveFiles.list()).thenReturn(driveFilesList);
        when(driveFilesList.setQ(anyString())).thenReturn(driveFilesList);
        when(driveFilesList.setFields(anyString())).thenReturn(driveFilesList);
        when(driveFilesList.setSupportsAllDrives(true)).thenReturn(driveFilesList);
        when(driveFilesList.setIncludeItemsFromAllDrives(true)).thenReturn(driveFilesList);
        when(driveFilesList.execute()).thenReturn(fileList);

        Optional<String> result = adapter.findFolderIdByName("Inexistente");

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Debe lanzar CoreTechnicalException cuando falla la búsqueda de carpeta")
    void shouldThrowExceptionWhenFindFolderFails() throws Exception{

        when(drive.files()).thenReturn(driveFiles);
        when(driveFiles.list()).thenThrow(new RuntimeException("Drive error"));

        CoreTechnicalException exception = assertThrows(
                CoreTechnicalException.class,
                () -> adapter.findFolderIdByName("Error")
        );

        assertTrue(exception.getMessage().contains("Google Drive"));
    }

    // ----------------------------------------------------------------------
    // findTicketFolderId
    // ----------------------------------------------------------------------

    @Test
    @DisplayName("Debe retornar el id de la subcarpeta Tickets cuando existe")
    void shouldReturnTicketFolderIdWhenExists() throws Exception {

        File folder = new File();
        folder.setId("ticket-folder-id");

        FileList fileList = new FileList();
        fileList.setFiles(List.of(folder));

        when(drive.files()).thenReturn(driveFiles);
        when(driveFiles.list()).thenReturn(driveFilesList);
        when(driveFilesList.setQ(anyString())).thenReturn(driveFilesList);
        when(driveFilesList.setFields(anyString())).thenReturn(driveFilesList);
        when(driveFilesList.setSupportsAllDrives(true)).thenReturn(driveFilesList);
        when(driveFilesList.setIncludeItemsFromAllDrives(true)).thenReturn(driveFilesList);
        when(driveFilesList.execute()).thenReturn(fileList);

        Optional<String> result = adapter.findTicketFolderId("parent-id");

        assertTrue(result.isPresent());
        assertEquals("ticket-folder-id", result.get());
    }

    @Test
    @DisplayName("Debe retornar empty cuando la subcarpeta Tickets no existe")
    void shouldReturnEmptyWhenTicketFolderNotFound() throws Exception {

        FileList fileList = new FileList();
        fileList.setFiles(List.of());

        when(drive.files()).thenReturn(driveFiles);
        when(driveFiles.list()).thenReturn(driveFilesList);
        when(driveFilesList.setQ(anyString())).thenReturn(driveFilesList);
        when(driveFilesList.setFields(anyString())).thenReturn(driveFilesList);
        when(driveFilesList.setSupportsAllDrives(true)).thenReturn(driveFilesList);
        when(driveFilesList.setIncludeItemsFromAllDrives(true)).thenReturn(driveFilesList);
        when(driveFilesList.execute()).thenReturn(fileList);

        Optional<String> result = adapter.findTicketFolderId("parent-id");

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Debe lanzar CoreTechnicalException cuando falla la búsqueda de subcarpeta Tickets")
    void shouldThrowExceptionWhenFindTicketFolderFails() throws Exception{

        when(drive.files()).thenReturn(driveFiles);
        when(driveFiles.list()).thenThrow(new RuntimeException("Drive error"));

        CoreTechnicalException exception = assertThrows(
                CoreTechnicalException.class,
                () -> adapter.findTicketFolderId("parent-id")
        );

        assertTrue(exception.getMessage().contains("Google Drive"));
    }

    // ----------------------------------------------------------------------
    // listPdfFiles
    // ----------------------------------------------------------------------

    @Test
    @DisplayName("Debe listar archivos PDF correctamente")
    void shouldListPdfFiles() throws Exception {

        File file = new File();
        file.setId("pdf-id");
        file.setName("ticket.pdf");

        FileList fileList = new FileList();
        fileList.setFiles(List.of(file));

        when(drive.files()).thenReturn(driveFiles);
        when(driveFiles.list()).thenReturn(driveFilesList);
        when(driveFilesList.setQ(anyString())).thenReturn(driveFilesList);
        when(driveFilesList.setFields(anyString())).thenReturn(driveFilesList);
        when(driveFilesList.setSupportsAllDrives(true)).thenReturn(driveFilesList);
        when(driveFilesList.setIncludeItemsFromAllDrives(true)).thenReturn(driveFilesList);
        when(driveFilesList.execute()).thenReturn(fileList);

        List<TicketFile> result = adapter.listPdfFiles("folder-id");

        assertEquals(1, result.size());
        assertEquals("pdf-id", result.get(0).id());
        assertEquals("ticket.pdf", result.get(0).name());
    }

    @Test
    @DisplayName("Debe lanzar CoreTechnicalException cuando falla el listado de PDFs")
    void shouldThrowExceptionWhenListPdfFilesFails() throws Exception{

        when(drive.files()).thenReturn(driveFiles);
        when(driveFiles.list()).thenThrow(new RuntimeException("Drive error"));

        CoreTechnicalException exception = assertThrows(
                CoreTechnicalException.class,
                () -> adapter.listPdfFiles("folder-id")
        );

        assertTrue(exception.getMessage().contains("Google Drive"));
    }

    // ----------------------------------------------------------------------
    // downloadFile
    // ----------------------------------------------------------------------

    @Test
    @DisplayName("Debe descargar archivo correctamente")
    void shouldDownloadFile() throws Exception {

        InputStream inputStream = new ByteArrayInputStream("content".getBytes());

        when(drive.files()).thenReturn(driveFiles);
        when(driveFiles.get("file-id")).thenReturn(driveFilesGet);
        when(driveFilesGet.executeMediaAsInputStream()).thenReturn(inputStream);

        InputStream result = adapter.downloadFile("file-id");

        assertNotNull(result);
    }

    @Test
    @DisplayName("Debe lanzar CoreTechnicalException cuando falla la descarga del archivo")
    void shouldThrowExceptionWhenDownloadFileFails() throws Exception{

        when(drive.files()).thenReturn(driveFiles);
        when(driveFiles.get("file-id")).thenThrow(new RuntimeException("Download error"));
        CoreTechnicalException exception = assertThrows(
                CoreTechnicalException.class,
                () -> adapter.downloadFile("file-id")
        );

        assertTrue(exception.getMessage().contains("Google Drive"));
    }

}
