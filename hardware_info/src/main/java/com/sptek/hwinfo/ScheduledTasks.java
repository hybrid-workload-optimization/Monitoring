package com.sptek.hwinfo;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.CentralProcessor.TickType;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HWDiskStore;
import oshi.hardware.HWPartition;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.LogicalVolumeGroup;
import oshi.hardware.VirtualMemory;
import oshi.software.os.FileSystem;
import oshi.software.os.OSFileStore;
import oshi.software.os.OSProcess;
import oshi.software.os.OSSession;
import oshi.software.os.OperatingSystem;
import oshi.software.os.OperatingSystem.ProcessFiltering;
import oshi.software.os.OperatingSystem.ProcessSorting;
import oshi.util.FormatUtil;
import oshi.util.Util;

/**
 * Scheduled tasks
 *
 * @author PSK
 */
@Component
public class ScheduledTasks {

	private final static Logger LOGGER = LoggerFactory.getLogger(ScheduledTasks.class);

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

	@Value("${fixed.delay.seconds}")
	private int fixedDelaySeconds;

	@Value("${log.extract.file.path}")
	private String logExtractFilePath;

	@Value("${os.path.type}")
	private String osPathType;

	static List<String> oshi = new ArrayList<>();

	/*
	 * [fixedDelay] 는 이전 수행이 종료된 시점부터 delay 후에 재 호출 [fixedRate] 는 이전 수행이 시작된 시점부터
	 * delay 후에 재 호출 fixedRate 로 지정 시 동시에 여러개가 돌 가능성이 존재
	 */
	// @Scheduled(fixedDelay = 10000)
	@Scheduled(fixedDelayString = "${fixed.delay.seconds}000")
	public void scheduleFixedRateTask() throws Exception {

		SystemInfo si = new SystemInfo();
		HardwareAbstractionLayer hal = si.getHardware();
		OperatingSystem os = si.getOperatingSystem();

		// printOperatingSystem(os);

		// printProcessor(hal.getProcessor());

		//LOGGER.info("Checking CPU...");
		printCpu(hal.getProcessor());

		// LOGGER.info("Checking Processes...");
		// printProcesses(os, hal.getMemory());

		//LOGGER.info("Checking Memory...");
		printMemory(hal.getMemory());

		//LOGGER.info("Checking Disks...");
		//printDisks(hal.getDiskStores());

		//LOGGER.info("Checking Logical Volume Groups ...");
		//printLVgroups(hal.getLogicalVolumeGroups());

		//LOGGER.info("Checking File System...");
		printFileSystem(os.getFileSystem());

		StringBuilder output = new StringBuilder();
		for (int i = 0; i < oshi.size(); i++) {
			output.append(oshi.get(i));
			if (oshi.get(i) != null && !oshi.get(i).endsWith("\n")) {
				//output.append('\n');
				output.append(" ");
			}
		}
		
		//LOGGER.info("Printing Operating System and Hardware Info:{}{}", '\n', output);
		/////////////////////////////////////////////////////////////////////

		String dir = System.getProperty("user.dir");
		String dirPath = dir + osPathType + logExtractFilePath;

		File LogFile = new File(dirPath);
		LogFile.mkdirs();

		SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		String logDate = dateFormat1.format(System.currentTimeMillis());

		String filePath = dir + osPathType + logExtractFilePath + osPathType + "hw_" + logDate + ".txt";

		FileWriter fstream = new FileWriter(filePath, true);
		BufferedWriter out = new BufferedWriter(fstream);
		InetAddress localIP = InetAddress.getLocalHost();

		
		out.write("LOG_TIME=" + dateFormat2.format(System.currentTimeMillis()) + " ");
		out.write("NODE_IP=" + localIP.getHostAddress() + " ");
		out.write("MESSAGE=" + output.toString());

		out.newLine();
		out.close();
		fstream.close();
		output = null;
		oshi.clear();

		//System.out.println("Fxied delay every " + fixedDelaySeconds + " second " + dateFormat.format(new Date()));
	}

	private static void printOperatingSystem(final OperatingSystem os) {
		oshi.add(String.valueOf(os));
		oshi.add("Booted: " + Instant.ofEpochSecond(os.getSystemBootTime()));
		oshi.add("Uptime: " + FormatUtil.formatElapsedSecs(os.getSystemUptime()));
		oshi.add("Running with" + (os.isElevated() ? "" : "out") + " elevated permissions.");
		oshi.add("Sessions:");
		for (OSSession s : os.getSessions()) {
			oshi.add(" " + s.toString());
		}
		oshi.add("");
	}

	private static void printCpu(CentralProcessor processor) {
		// oshi.add("Context Switches/Interrupts: " + processor.getContextSwitches() + "
		// / " + processor.getInterrupts());

		long[] prevTicks = processor.getSystemCpuLoadTicks();
		long[][] prevProcTicks = processor.getProcessorCpuLoadTicks();
		// oshi.add("CPU, IOWait, and IRQ ticks @ 0 sec:" + Arrays.toString(prevTicks));
		// Wait a second...
		Util.sleep(1000);
		long[] ticks = processor.getSystemCpuLoadTicks();
		// oshi.add("CPU, IOWait, and IRQ ticks @ 1 sec:" + Arrays.toString(ticks));
		long user = ticks[TickType.USER.getIndex()] - prevTicks[TickType.USER.getIndex()];
		long nice = ticks[TickType.NICE.getIndex()] - prevTicks[TickType.NICE.getIndex()];
		long sys = ticks[TickType.SYSTEM.getIndex()] - prevTicks[TickType.SYSTEM.getIndex()];
		long idle = ticks[TickType.IDLE.getIndex()] - prevTicks[TickType.IDLE.getIndex()];
		long iowait = ticks[TickType.IOWAIT.getIndex()] - prevTicks[TickType.IOWAIT.getIndex()];
		long irq = ticks[TickType.IRQ.getIndex()] - prevTicks[TickType.IRQ.getIndex()];
		long softirq = ticks[TickType.SOFTIRQ.getIndex()] - prevTicks[TickType.SOFTIRQ.getIndex()];
		long steal = ticks[TickType.STEAL.getIndex()] - prevTicks[TickType.STEAL.getIndex()];
		long totalCpu = user + nice + sys + idle + iowait + irq + softirq + steal;

		oshi.add(String.format(
				"CPU User: %.1f%% CPU Nice: %.1f%% CPU System: %.1f%% CPU Idle: %.1f%% CPU IOwait: %.1f%% CPU IRQ: %.1f%% CPU SoftIRQ: %.1f%% CPU Steal: %.1f%%",
				100d * user / totalCpu, 100d * nice / totalCpu, 100d * sys / totalCpu, 100d * idle / totalCpu,
				100d * iowait / totalCpu, 100d * irq / totalCpu, 100d * softirq / totalCpu, 100d * steal / totalCpu));
		oshi.add(String.format("CPU load: %.1f%%", processor.getSystemCpuLoadBetweenTicks(prevTicks) * 100));
		double[] loadAverage = processor.getSystemLoadAverage(3);
		// oshi.add("CPU load averages:" + (loadAverage[0] < 0 ? " N/A" :
		// String.format(" %.2f", loadAverage[0]))
		// + (loadAverage[1] < 0 ? " N/A" : String.format(" %.2f", loadAverage[1]))
		// + (loadAverage[2] < 0 ? " N/A" : String.format(" %.2f", loadAverage[2])));
		// per core CPU
		// StringBuilder procCpu = new StringBuilder("CPU load per processor:");
		// double[] load = processor.getProcessorCpuLoadBetweenTicks(prevProcTicks);
		// for (double avg : load) {
		// procCpu.append(String.format(" %.1f%%", avg * 100));
		// }
		// oshi.add(procCpu.toString());
		// long freq = processor.getProcessorIdentifier().getVendorFreq();
		// if (freq > 0) {
		// oshi.add("Vendor Frequency: " + FormatUtil.formatHertz(freq));
		// }
		// freq = processor.getMaxFreq();
		// if (freq > 0) {
		// oshi.add("Max Frequency: " + FormatUtil.formatHertz(freq));
		// }
		// long[] freqs = processor.getCurrentFreq();
		// if (freqs[0] > 0) {
		// StringBuilder sb = new StringBuilder("Current Frequencies: ");
		// for (int i = 0; i < freqs.length; i++) {
		// if (i > 0) {
		// sb.append(", ");
		// }
		// sb.append(FormatUtil.formatHertz(freqs[i]));
		// }
		// oshi.add(sb.toString());
		// }
	}

	private static void printProcessor(CentralProcessor processor) {
		oshi.add(processor.toString());
	}

	private static void printProcesses(OperatingSystem os, GlobalMemory memory) {
		OSProcess myProc = os.getProcess(os.getProcessId());
		// current process will never be null. Other code should check for null here
		oshi.add(
				"My PID: " + myProc.getProcessID() + " with affinity " + Long.toBinaryString(myProc.getAffinityMask()));
		oshi.add("Processes: " + os.getProcessCount() + ", Threads: " + os.getThreadCount());
		// Sort by highest CPU
		List<OSProcess> procs = os.getProcesses(ProcessFiltering.ALL_PROCESSES, ProcessSorting.CPU_DESC, 5);
		oshi.add("   PID  %CPU %MEM       VSZ       RSS Name");
		for (int i = 0; i < procs.size() && i < 5; i++) {
			OSProcess p = procs.get(i);
			oshi.add(String.format(" %5d %5.1f %4.1f %9s %9s %s", p.getProcessID(),
					100d * (p.getKernelTime() + p.getUserTime()) / p.getUpTime(),
					100d * p.getResidentSetSize() / memory.getTotal(), FormatUtil.formatBytes(p.getVirtualSize()),
					FormatUtil.formatBytes(p.getResidentSetSize()), p.getName()));
		}
		OSProcess p = os.getProcess(os.getProcessId());
		oshi.add("Current process arguments: ");
		for (String s : p.getArguments()) {
			oshi.add("  " + s);
		}
		oshi.add("Current process environment: ");
		for (Entry<String, String> e : p.getEnvironmentVariables().entrySet()) {
			oshi.add("  " + e.getKey() + "=" + e.getValue());
		}
	}

	private static void printMemory(GlobalMemory memory) {
		oshi.add("Physical Memory: " + memory.toString());
		VirtualMemory vm = memory.getVirtualMemory();
		oshi.add("Virtual Memory: " + vm.toString());
		/*
		 * List<PhysicalMemory> pmList = memory.getPhysicalMemory(); if
		 * (!pmList.isEmpty()) { oshi.add("Physical Memory: "); for (PhysicalMemory pm :
		 * pmList) { oshi.add(" " + pm.toString()); } }
		 */
	}

	private static void printDisks(List<HWDiskStore> list) {
		//oshi.add("Disks:");
		for (HWDiskStore disk : list) {
			oshi.add(" " + disk.toString());

			List<HWPartition> partitions = disk.getPartitions();
			for (HWPartition part : partitions) {
				oshi.add(" |-- " + part.toString());
			}
		}

	}

	private static void printLVgroups(List<LogicalVolumeGroup> list) {
		if (!list.isEmpty()) {
			oshi.add("Logical Volume Groups:");
			for (LogicalVolumeGroup lvg : list) {
				oshi.add(" " + lvg.toString());
			}
		}
	}

	private static void printFileSystem(FileSystem fileSystem) {
		//oshi.add("File System");

		//oshi.add(String.format(" File Descriptors: %d/%d", fileSystem.getOpenFileDescriptors(),
		//		fileSystem.getMaxFileDescriptors()));

		for (OSFileStore fs : fileSystem.getFileStores()) {
			long usable = fs.getUsableSpace();
			long total = fs.getTotalSpace();
			oshi.add(String.format(
					" %s (%s) [%s] %s of %s free (%.1f%%), %s of %s files free (%.1f%%) is %s "
							+ (fs.getLogicalVolume() != null && fs.getLogicalVolume().length() > 0 ? "[%s]" : "%s")
							+ " and is mounted at %s",
					fs.getName(), fs.getDescription().isEmpty() ? "file system" : fs.getDescription(), fs.getType(),
					FormatUtil.formatBytes(usable), FormatUtil.formatBytes(fs.getTotalSpace()), 100d * usable / total,
					FormatUtil.formatValue(fs.getFreeInodes(), ""), FormatUtil.formatValue(fs.getTotalInodes(), ""),
					100d * fs.getFreeInodes() / fs.getTotalInodes(), fs.getVolume(), fs.getLogicalVolume(),
					fs.getMount()));
		}
	}
}