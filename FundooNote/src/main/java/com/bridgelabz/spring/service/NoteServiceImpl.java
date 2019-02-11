package com.bridgelabz.spring.service;

import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.bridgelabz.spring.Utility.TokenGenerator;
import com.bridgelabz.spring.dao.NoteDao;
import com.bridgelabz.spring.dao.UserDao;
import com.bridgelabz.spring.model.Label;
import com.bridgelabz.spring.model.Note;
import com.bridgelabz.spring.model.User;

@Service
public class NoteServiceImpl implements NoteService {

	@Autowired
	private NoteDao noteDao;

	@Autowired
	private TokenGenerator tokenGenerator;

	@Autowired
	private UserDao userDao;

	@Transactional
	public boolean createNote(String token, Note note, HttpServletRequest request) {
		int id = tokenGenerator.authenticateToken(token);
		User user = userDao.getUserById(id);
		if (user != null) {
			note.setUserId(user);
			int i = noteDao.createNote(note);
			if (i > 0) {
				return true;
			}
		}
		return false;
	}

	@Transactional
	public Note updateNote(String token, int noteId, Note note, HttpServletRequest request) {
		int id = tokenGenerator.authenticateToken(token);
		User user = userDao.getUserById(id);
		if (user != null) {
			Note newNote = noteDao.getNoteById(noteId);
			if (newNote != null) {
				newNote.setTitle(note.getTitle());
				newNote.setDiscription(note.getDiscription());
				newNote.setArchive(note.isArchive());
				newNote.setPinned(note.isPinned());
				newNote.setInTrash(note.isInTrash());
				noteDao.updateNote(newNote);
			}
			return newNote;
		}
		return null;
	}

	@Transactional
	public Note deleteNote(String token, int noteId, HttpServletRequest request) {
		int id = tokenGenerator.authenticateToken(token);
		User user = userDao.getUserById(id);
		if (user != null) {
			Note newNote = noteDao.getNoteById(noteId);
			if (newNote != null) {
				noteDao.deleteNote(noteId);
			}
			return newNote;
		}
		return null;
	}

	@Transactional
	public List<Note> retrieveNote(String token, HttpServletRequest request) {
		int id = tokenGenerator.authenticateToken(token);
		User user = userDao.getUserById(id);
		if (user != null) {
			List<Note> notes = noteDao.retrieveNote(id);
			if (!notes.isEmpty()) {
				return notes;
			}
		}
		return null;
	}

	@Transactional
	public boolean createLabel(String token, Label label, HttpServletRequest request) {
		int id = tokenGenerator.authenticateToken(token);
		User user = userDao.getUserById(id);
		if (user != null) {
			label.setUserId(user);
			int i = noteDao.createLabel(label);
			if (i > 0) {
				return true;
			}
		}
		return false;
	}

	@Transactional
	public Label updateLabel(String token, int labelId, Label label, HttpServletRequest request) {
		int id = tokenGenerator.authenticateToken(token);
		User user = userDao.getUserById(id);
		if (user != null) {
			Label newLabel = noteDao.getLabelById(labelId);
			if (newLabel != null) {
				newLabel.setLabelName(label.getLabelName());
				noteDao.updateLabel(newLabel);
			}
			return newLabel;
		}
		return null;
	}

	@Transactional
	public Label deleteLabel(String token, int labelId, HttpServletRequest request) {
		int id = tokenGenerator.authenticateToken(token);
		User user = userDao.getUserById(id);
		if (user != null) {
			Label newLabel = noteDao.getLabelById(labelId);
			if (newLabel != null) {
				noteDao.deleteLabel(labelId);
			}
			return newLabel;
		}
		return null;
	}

	@Transactional
	public List<Label> retrieveLabel(String token, HttpServletRequest request) {
		int id = tokenGenerator.authenticateToken(token);
		User user = userDao.getUserById(id);
		if (user != null) {
			List<Label> labels = noteDao.retrieveLabel(id);
			if (!labels.isEmpty()) {
				return labels;
			}
		}
		return null;
	}

	@Transactional
	public boolean mappingNoteLabel(String token, int noteId, int labelId, HttpServletRequest request) {
		int id = tokenGenerator.authenticateToken(token);
		User user = userDao.getUserById(id);
		if (user != null) {
			Note note = noteDao.getNoteById(noteId);
			Label label = noteDao.getLabelById(labelId);
			List<Label> labels = note.getLabels();
			labels.add(label);
			if (!labels.isEmpty()) {
				note.setLabels(labels);
				noteDao.updateNote(note);
				return true;
			}
		}
		return false;
	}

	public boolean removeNoteLabel(String token, int noteId, int labelId, HttpServletRequest request) {
		int id = tokenGenerator.authenticateToken(token);
		User user = userDao.getUserById(id);
		if (user != null) {
			Note note = noteDao.getNoteById(noteId);
			List<Label> labels = note.getLabels();
			if (!labels.isEmpty()) {
				labels = labels.stream().filter(label -> label.getLabelId() != labelId).collect(Collectors.toList());
				note.setLabels(labels);
				noteDao.updateNote(note);
				return true;
			}
		}
		return false;
	}

}
